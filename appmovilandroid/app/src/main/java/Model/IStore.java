package Model;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

/**
 * Created by ianne on 1/04/2018.
 */

interface IStore {
    User getUser(String uid);
    ArrayList<User> getUsers(ArrayList<String> userUIDs);

    ArrayList<String> getAllProjectsUIDs();
    Project getProject(String uid);
    ArrayList<Project> getProjects(ArrayList<String> uids);

    void createProject(Project project) throws Errors.CreateProjectException;

    User authenticate(String username, String password) throws Errors.AuthException;
    boolean register(User user, String password) throws  Errors.RegisterException;

    void updateUser(User user);
    void updateProject(Project project);
    void deleteProject(Project project);
}

interface IAsyncStore {
    Task<User> getUser(String uid);
    Task<ArrayList<User>> getUsers(ArrayList<String> userUIDs);

    Task<ArrayList<String>> getAllProjectsUIDs();
    Task<Project> getProject(String uid);
    Task<ArrayList<Project>> getProjects(ArrayList<String> uids);

    Task<Void> createProject(Project project) throws Errors.CreateProjectException;

    Task<User> authenticate(String username, String password) throws Errors.AuthException;
    Task<User> register(User user, String password) throws  Errors.RegisterException;
    Task<Void> resetPassword(String email);

    Task<Void> updateUser(User user);
    Task<Void> updateProject(Project project);
    Task<Void> deleteProject(Project project);
}
