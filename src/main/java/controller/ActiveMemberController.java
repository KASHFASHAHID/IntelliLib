package controller;

import model.ActiveMember;
import service.ActiveMemberService;

import java.util.List;

public class ActiveMemberController {

    private final ActiveMemberService service;

    public ActiveMemberController() {
        service = new ActiveMemberService();
    }

    public List<ActiveMember> getAllActiveMembers() {
        return service.getAllActiveMembers();
    }

    public List<ActiveMember> getAllManageableMembers() {
        return service.getAllManageableMembers();
    }

    public String getReactivationBlockReason(
            String memberUserId
    ) {

        return service.getReactivationBlockReason(
                memberUserId
        );
    }

    public boolean suspendMember(
            String memberUserId,
            String reason,
            String performedBy
    ) {

        return service.suspendMember(
                memberUserId,
                reason,
                performedBy
        );
    }

    public boolean blockMember(
            String memberUserId,
            String reason,
            String performedBy
    ) {

        return service.blockMember(
                memberUserId,
                reason,
                performedBy
        );
    }

    public boolean reactivateMember(
            String memberUserId,
            String reason,
            String performedBy
    ) {

        return service.reactivateMember(
                memberUserId,
                reason,
                performedBy
        );
    }
}