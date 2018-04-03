package Model;

import java.util.ArrayList;

/**
 * Created by ianne on 2/04/2018.
 */

public class Store implements IStore {
    @Override
    public User getUser(String uid) {
        return null;
    }

    @Override
    public ArrayList<String> getAllProjectsUIDs() {
        return null;
    }

    @Override
    public Project getProject(String uid) {
        return null;
    }

    @Override
    public ArrayList<Project> getProjects(ArrayList<String> uids) {
        return null;
    }

    @Override
    public boolean authenticate(String username, String password) throws Errors.AuthException {
        return false;
    }

    @Override
    public void updateProject(Project project) {

    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void register(User user, String password) throws Errors.RegisterException {

    }

    @Override
    public void createProject(Project project) {

    }
}
