package Model;

import java.util.ArrayList;

/**
 * Created by ianne on 1/04/2018.
 */

interface Store {
    User getUser(String uid);

    ArrayList<String> getAllProjectsUIDs();
    Project getProject(String uid);
    ArrayList<Project> getProjects(ArrayList<String> uids);
    void createProject(Project project);

    boolean authenticate(String username, String password) throws Errors.AuthException;
    void register(User user) throws  Errors.RegisterException;

}
