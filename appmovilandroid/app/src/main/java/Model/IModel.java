package Model;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

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
    void reviewApplicant(Project project, User applicant, boolean accept);

    Task<Boolean> authenticate(String username, String password) throws Errors.AuthException;
    void logout();
    User getCurrentUser();
    void register(User user, String password) throws  Errors.RegisterException;

    Task<ArrayList<Project>> getOwnedProjects();
    Task<HashMap<Project, Boolean>> getNotifications();
}
