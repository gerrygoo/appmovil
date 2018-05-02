package Model;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

/**
 * Created by ianne on 1/04/2018.
 */

public interface IModel {
    // static methods in interface not available in API Below 24
    // IModel getInstance();

    Task<ArrayList<Project>> getAvailableProjects();
    Task<ArrayList<Project>> getMyProjects();
    void viewNotification(Project project);

    Task<ArrayList<User>> getApplicants(String projectUID);
    Task<ArrayList<User>> getTeam(String projectUID);

    void reviewProject(Project project, boolean accept);
    Task<Void> createProject(Project project) throws Errors.CreateProjectException;
    Task<Void> updateProject(Project project);
    Task<Void> deleteProject(Project project);
    void reviewApplicant(Project project, User applicant, boolean accept);

    Task<Boolean> authenticate(String username, String password) throws Errors.AuthException;
    void logout();
    User getCurrentUser();
    Task<Void> updateCurrentUser();
    Task<User> getUserByID(String UID);
    void register(User user, String password) throws  Errors.RegisterException;

    Task<ArrayList<Project>> getOwnedProjects();
    Task<Void> rateUser(String uid, double rating);
    //Task<HashMap<Project, Boolean>> getNotifications();
}
