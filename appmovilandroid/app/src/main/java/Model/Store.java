package Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by ianne on 2/04/2018.
 */

public class Store implements IStore {

    private final ArrayList<Project> projects;
    private final ArrayList<User> users;

    private HashMap<User, String> passwords;

    private Random random;

    //this has to be package private.
    Store(){
        random = new Random();
        passwords = new HashMap<>();
        users = new ArrayList<>();
        projects = new ArrayList<Project>();

        users.add(new User("iansa", "Ian"));
        users.add(new User("ger@ger.com", "Gerry"));

        passwords.put(users.get(0), "iansa");
        passwords.put(users.get(1), "gerrygoo");

        int index = 0;

        projects.add(MicrosoftProject(index++));
        projects.add(GoogleProject(index++));
        projects.add(MicrosoftProject(index++));
        projects.add(GoogleProject(index++));
        projects.add(MicrosoftProject(index++));
        projects.add(GoogleProject(index++));
        projects.add(MicrosoftProject(index++));
        projects.add(GoogleProject(index++));
        projects.add(MicrosoftProject(index++));
        projects.add(GoogleProject(index++));
        projects.add(MicrosoftProject(index++));
        projects.add(GoogleProject(index++));
    }

    @Override
    public User getUser(String uid) {
        for(User user: users){
            if(user.getUID().equals(uid)){
                return user;
            }
        }
        return null;
    }

    @Override
    public ArrayList<User> getUsers(ArrayList<String> userUIDs) {
        return null;
    }

    @Override
    public ArrayList<String> getAllProjectsUIDs() {
        ArrayList<String> result = new ArrayList<>(projects.size());
        for (Project project: projects){
            result.add(project.getUID());
        }
        return result;
    }

    @Override
    public Project getProject(String uid) {
        for (Project project: projects) {
            if (project.getUID().equals(uid)){
                return project;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Project> getProjects(ArrayList<String> uids) {
        ArrayList<Project> result = new ArrayList<>(uids.size());
        for (Project project: projects) {
            if (uids.contains(project.getUID())){
                result.add(project);
            }
        }
        return result;
    }

    @Override
    public User authenticate(String email, String password) throws Errors.AuthException {
        if(email.isEmpty() || password.isEmpty()){
            throw new Errors.AuthException("username or password missing", Errors.AuthError.MissingItems);
        }
        for(User user: users){
            if(user.getEmail().equals(email)){
                if(password.equals(passwords.get(user))){
                    return user;
                }
                throw new Errors.AuthException("username or password missing", Errors.AuthError.InvalidUsername);
            }
        }
        throw new Errors.AuthException("username or password missing", Errors.AuthError.InvalidUsername);
    }

    @Override
    public void updateProject(Project project) {
        Project current;
        for (int i = 0; i < projects.size(); i++) {
            current = projects.get(i);
            if(current.equals(project)){
                projects.set(i, project);
            }
        }
    }

    @Override
    public void updateUser(User user) {
        User current;
        for (int i = 0; i < users.size(); i++) {
            current = users.get(i);
            if(current.equals(user)){
                users.set(i, user);
            }
        }
    }

    @Override
    public boolean register(User user, String password) throws Errors.RegisterException {
        if(user.getName().isEmpty() || user.getEmail().isEmpty() || password.isEmpty()){
            throw new Errors.RegisterException("Item is missing", Errors.RegisterError.MissingItem);
        }
        if(!verifyEmail(user.getEmail())){
            throw new Errors.RegisterException("Account already exists", Errors.RegisterError.AccountAlreadyExists);
        }
        if(!verifyPassword(password)){
            throw new Errors.RegisterException("Invalid password", Errors.RegisterError.InvalidPassword);
        }
        user.setUID(random.nextInt(1000000) + "");
        users.add(user);
        passwords.put(user, password);
        return true;
    }

    private boolean verifyEmail(String email) {
        for(User user: users){
            if(user.getEmail().equalsIgnoreCase(email)){
                return false;
            }
        }
        return true;
    }

    private boolean verifyPassword(String password) {
        return password.length() >= 8;
    }

    @Override
    public void createProject(Project project) throws Errors.CreateProjectException {
        if(project == null || project.getOwnerUID() == null || project.getStartDate() == null || project.getEndDate() == null){
            throw new Errors.CreateProjectException("Missing data", Errors.CreateProjectError.MissingItems);
        }
        if(project.getUID().isEmpty() || project.getTitle().isEmpty() || project.getDescription().isEmpty() ||
                project.getDescription().isEmpty() || project.getEndDate().before(project.getStartDate())){
            throw new Errors.CreateProjectException("Invalid data", Errors.CreateProjectError.InvalidProject);
        }
        projects.add(project);
        Log.e("Project", "Project Added: " + projects.size() +" items");
    }

    public void deleteProject(Project project)
    {
        projects.remove(projects.indexOf(project));
    }

    private Project GoogleProject(int index){
        Calendar endDate = Calendar.getInstance();


        ArrayList<String> positions = new ArrayList<>();
        positions.add("Programmer");
        positions.add("Product Manager");
        positions.add("Experience Designer");

        return new Project(
                random.nextInt(100000) + "",
                null,
                "Smart Cars" + index,
                "https://pmcvariety.files.wordpress.com/2015/08/google-placeholder-logo.jpg?w=1000&h=563&crop=1",
                positions,
                "The project focuses on building a self driving car, in which you are required to know Machine Learning and Artificial Intelligence Algorithms in order to be eligible for this project",
                "Mountain View, California, United States",
                Calendar.getInstance().getTime(),
                endDate.getTime());
    }

    private Project MicrosoftProject(int index){
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 18);

        Calendar startDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 8);
        ArrayList<String> positions = new ArrayList<>();
        positions.add("Programmer");
        positions.add("Program Manager");
        positions.add("Tester");

        return new Project(
                random.nextInt(100000) + "",
                null,
                "Cortana Search" + index,
                "https://mspoweruser.com/wp-content/uploads/2016/09/Webgroesse_HighRes_Microsoft12711.jpg",
                positions,
                "This project focuses on implementing a Natural Language search for Cortana, for this we require that you have knowledge and background on Natural Language Processing or Artificial Intelligence",
                "Redmond, Washington, United States",
                startDate.getTime(),
                endDate.getTime());
    }
}
