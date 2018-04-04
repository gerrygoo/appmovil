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
    void createProject(Project project);

    boolean authenticate(String username, String password) throws Errors.AuthException;
    void register(User user, String password) throws  Errors.RegisterException;

    void updateUser(User user);
    void updateProject(Project project);
}
