package Model;

import java.util.ArrayList;

/**
 * Created by ianne on 2/04/2018.
 */

public class Model implements IModel {

    private IStore Store;
    private static Model instance;
    private User currentUser;

    private Model(IStore store){
        Store = store;
    }

    public static Model getInstance() {
        if(instance == null){
            instance = new Model(new Store());
        }
        return instance;
    }

    @Override
    public ArrayList<Project> getAvailableProjects() {
        ArrayList<String> uids = Store.getAllProjectsUIDs();
        ArrayList<String> pendingProjectsUIDs = new ArrayList<>(uids.size());

        for (String uid: uids) {
            if(!currentUser.hasReviewedProject(uid)){
                pendingProjectsUIDs.add(uid);
            }
        }

        return  Store.getProjects(pendingProjectsUIDs);
    }

    @Override
    public ArrayList<Project> getMyProjects() {
        return currentUser.getProjectsMember();
    }

    @Override
    public void viewNotificaction(Project project) {
        currentUser.viewNotification(project);
        Store.updateUser(currentUser);
    }

    @Override
    public ArrayList<User> getApplicants(String projectUID) {
        return Store.getProject(projectUID).getApplicants();
    }

    @Override
    public ArrayList<User> getTeam(String projectUID) {
        return Store.getProject(projectUID).getTeam();
    }

    @Override
    public boolean authenticate(String username, String password) throws Errors.AuthException {
        if(username.isEmpty() || password.isEmpty()){
            throw new Errors.AuthException("Username or password is Missing", Errors.AuthError.MissingItems);
        }
        try {
            this.currentUser = Store.authenticate(username, password);
            return true;
        } catch (Errors.AuthException exception){
            throw exception;
        }
    }

    @Override
    public void logout() {
        this.currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    // TODO
    @Override
    public void register(User user, String password) throws Errors.RegisterException {
        if(user == null || password.isEmpty()){
            throw new Errors.RegisterException("Missing user or password", Errors.RegisterError.MissingItem);
        }
        Store.register(user,password);
    }

    @Override
    public void reviewApplicant(Project project, User applicant, boolean accept) {
        if(accept) {
            project.addTeamMember(applicant);
            applicant.addProject(project);
            Store.updateUser(applicant);
        }
        project.removeApplicant(applicant);
        Store.updateProject(project);
    }

    @Override
    public void createProject(Project project) throws Errors.CreateProjectException {
        Store.createProject(project);
        currentUser.addProjectOwned(project);
        Store.updateUser(currentUser);
    }

    @Override
    public void reviewProject(Project project, boolean accept) {
        currentUser.reviewProject(project, accept);
        if(accept)project.addApplicant(currentUser);
        Store.updateUser(currentUser);
        project.addApplicant(currentUser);
    }


}
