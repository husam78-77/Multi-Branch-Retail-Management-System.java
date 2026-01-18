package service;

import dao.BranchDAO;
import model.Branch;
import util.PermissionManager;

import java.util.List;

public class BranchService {

    private final BranchDAO branchDAO = new BranchDAO();

    public List<Branch> getAllBranches() {
        PermissionManager.requireAdmin("view branches");
        return branchDAO.getAll();
    }

    public void addBranch(Branch branch) {
        PermissionManager.requireAdmin("add branch");
        branchDAO.insert(branch);
    }
    public void updateBranch(Branch branch) {
        PermissionManager.requireAdmin("update branch");
        branchDAO.update(branch);
    }

    public void deleteBranch(int branchId) {
        PermissionManager.requireAdmin("delete branch");
        branchDAO.toggleBranchStatus(branchId);
    }

    public List<Branch> searchBranches(String keyword) {
        PermissionManager.requireAdmin("search branches");
        return branchDAO.searchByName(keyword);
    }

    public List<Branch> getBranchesForSelection() {
        PermissionManager.requireAdmin("access branch selection");
        return branchDAO.getAll();
    }
}
