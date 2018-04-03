package Model;

import java.util.ArrayList;

/**
 * Created by ianne on 1/04/2018.
 */

public interface IModel {
    // static methods in interface not available in API Below 24
    // IModel getInstance();

    ArrayList<Project> getAvailableProjects();
    ArrayList<Project> getMyProjects();

    ArrayList<User> getApplicants(String projectUID);
    ArrayList<User> getTeam(String projectUID);

    void reviewProject(Project project, boolean accept);
    void createProject(Project project) throws Errors.CreateProjectException;
    void reviewApplicant(Project project, User applicant, boolean accept);

    boolean authenticate(String username, String password) throws Errors.AuthException;
    void logout();
    User getCurrentUser();
    void register(User user, String password) throws  Errors.RegisterException;
}
