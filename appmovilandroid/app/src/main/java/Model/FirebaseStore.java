package Model;

import java.util.ArrayList;

class FirebaseStore implements IStore {

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
    public void createProject(Project project) throws Errors.CreateProjectException {

    }

    @Override
    public User authenticate(String username, String password) throws Errors.AuthException {
        return null;
    }

    @Override
    public boolean tryAuthenticate(String username, String password) {
        return false;
    }

    @Override
    public void register(User user, String password) throws Errors.RegisterException {

    }

    @Override
    public boolean tryRegister(User user, String password) {
        return false;
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void updateProject(Project project) {

    }
}
