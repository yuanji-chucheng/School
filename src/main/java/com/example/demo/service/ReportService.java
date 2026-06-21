package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.PageResult;
import com.example.demo.dto.ReportHandleRequest;
import com.example.demo.dto.ReportRequest;
import com.example.demo.entity.HelpRequest;
import com.example.demo.entity.Item;
import com.example.demo.entity.Report;
import com.example.demo.mapper.HelpRequestMapper;
import com.example.demo.mapper.ItemMapper;
import com.example.demo.mapper.ReportMapper;
import com.example.demo.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 举报服务 */
@Service
public class ReportService {

    private final ReportMapper reportMapper;
    private final ItemMapper itemMapper;
    private final HelpRequestMapper helpRequestMapper;
    private final NotificationService notificationService;

    public ReportService(ReportMapper reportMapper, ItemMapper itemMapper,
                         HelpRequestMapper helpRequestMapper, NotificationService notificationService) {
        this.reportMapper = reportMapper;
        this.itemMapper = itemMapper;
        this.helpRequestMapper = helpRequestMapper;
        this.notificationService = notificationService;
    }

    public void submit(ReportRequest req) {
        Long reporterId = UserContext.getUserId();
        if ("ITEM".equals(req.getTargetType())) {
            Item item = itemMapper.findById(req.getTargetId());
            if (item == null) throw new BusinessException("物品不存在");
            if (item.getSellerId().equals(reporterId)) throw new BusinessException("不能举报自己的物品");
        } else if ("HELP".equals(req.getTargetType())) {
            HelpRequest helpReq = helpRequestMapper.findById(req.getTargetId());
            if (helpReq == null) throw new BusinessException("互助帖不存在");
            if (helpReq.getUserId().equals(reporterId)) throw new BusinessException("不能举报自己的帖子");
        }
        Report report = new Report();
        report.setReporterId(reporterId);
        report.setTargetType(req.getTargetType());
        report.setTargetId(req.getTargetId());
        report.setReason(req.getReason());
        report.setStatus(0);
        reportMapper.insert(report);
    }

    public Report getById(Long id) {
        Report report = reportMapper.findById(id);
        if (report == null) throw new BusinessException("举报不存在");
        return report;
    }

    public PageResult<Report> pending(int page, int size) {
        if (!UserContext.isAdmin()) throw new BusinessException(403, "需要管理员权限");
        int offset = (page - 1) * size;
        List<Report> rows = reportMapper.findPending(offset, size);
        return new PageResult<>(rows, reportMapper.countPending());
    }

    @Transactional
    public void handle(Long id, ReportHandleRequest req) {
        if (!UserContext.isAdmin()) throw new BusinessException(403, "需要管理员权限");
        Report report = reportMapper.findById(id);
        if (report == null) throw new BusinessException("举报不存在");
        if (report.getStatus() != 0) throw new BusinessException("该举报已处理");

        String action = req.getAction();
        if (!"REMOVE".equals(action) && !"NO_VIOLATION".equals(action)) {
            throw new BusinessException("无效的处理操作");
        }

        Long posterId;
        String posterMsg;
        String reporterMsg;
        String handleResult;

        if ("REMOVE".equals(action)) {
            if ("ITEM".equals(report.getTargetType())) {
                Item item = itemMapper.findById(report.getTargetId());
                if (item == null) throw new BusinessException("被举报物品不存在");
                item.setStatus(4);
                itemMapper.update(item);
                posterId = item.getSellerId();
                posterMsg = "您的物品《" + item.getTitle() + "》经举报审核已被下架";
                reporterMsg = "您举报的物品《" + item.getTitle() + "》经审核已下架";
                handleResult = "审核下架";
            } else if ("HELP".equals(report.getTargetType())) {
                HelpRequest helpReq = helpRequestMapper.findById(report.getTargetId());
                if (helpReq == null) throw new BusinessException("被举报互助帖不存在");
                helpRequestMapper.updateStatus(report.getTargetId(), 5);
                posterId = helpReq.getUserId();
                posterMsg = "您的互助帖《" + helpReq.getTitle() + "》经举报审核已被下架";
                reporterMsg = "您举报的互助帖《" + helpReq.getTitle() + "》经审核已下架";
                handleResult = "审核下架";
            } else {
                throw new BusinessException("不支持的举报类型");
            }
        } else {
            if ("ITEM".equals(report.getTargetType())) {
                Item item = itemMapper.findById(report.getTargetId());
                if (item == null) throw new BusinessException("被举报物品不存在");
                posterId = item.getSellerId();
                posterMsg = "您的物品《" + item.getTitle() + "》经举报审核无违规";
                reporterMsg = "您举报的物品《" + item.getTitle() + "》经审核无违规";
            } else if ("HELP".equals(report.getTargetType())) {
                HelpRequest helpReq = helpRequestMapper.findById(report.getTargetId());
                if (helpReq == null) throw new BusinessException("被举报互助帖不存在");
                posterId = helpReq.getUserId();
                posterMsg = "您的互助帖《" + helpReq.getTitle() + "》经举报审核无违规";
                reporterMsg = "您举报的互助帖《" + helpReq.getTitle() + "》经审核无违规";
            } else {
                throw new BusinessException("不支持的举报类型");
            }
            handleResult = "审核无违规";
        }

        notificationService.send(posterId, "举报处理结果", posterMsg, "REPORT");
        notificationService.send(report.getReporterId(), "举报处理结果", reporterMsg, "REPORT");

        report.setStatus(1);
        report.setHandleResult(handleResult);
        reportMapper.update(report);
    }
}
