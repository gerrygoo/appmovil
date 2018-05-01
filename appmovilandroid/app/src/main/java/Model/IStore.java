package Model;

import java.util.ArrayList;

/**
 * Created by ianne on 1/04/2018.
 */

interface IStore {
    User getUser(String uid);

    ArrayList<String> getAllProjectsUIDs();
    Project getProject(String uid);
    ArrayList<Project> getProjects(ArrayList<String> uids);

    void createProject(Project project) throws Errors.CreateProjectException;

    User authenticate(String username, String password) throws Errors.AuthException;
    boolean tryAuthenticate(String username, String password);
    void register(User user, String password) throws  Errors.RegisterException;
    boolean tryRegister(User user, String password);

    void updateUser(User user);
    void updateProject(Project project);
    void deleteProject(Project project);
}
