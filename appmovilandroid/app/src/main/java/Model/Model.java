package Model;

import java.util.ArrayList;

/**
 * Created by ianne on 1/04/2018.
 */

public interface Model {
    Model getInstance();

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
    void register(User user) throws  Errors.RegisterException;
}
