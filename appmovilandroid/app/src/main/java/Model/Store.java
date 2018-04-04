package Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 * Created by ianne on 2/04/2018.
 */

public class Store implements IStore {

    private final ArrayList<Project> projects;
    private final ArrayList<User> users;

    private HashMap<User, String> passwords;

    private Random random;

    Store(){
        random = new Random();
        passwords = new HashMap<>();
        users = new ArrayList<>();
        projects = new ArrayList<Project>();

        users.add(new User("1", "iansa", "ian@ian.com", "Ian", "Microsoft", 5, false));
        users.add(new User("2", "gerrygoo", "ger@ger.com", "Gerry", "Sadeira", 4, false));

        passwords.put(users.get(0), "iansa");
        passwords.put(users.get(1), "gerrygoo");

        projects.add(MicrosoftProject());
        projects.add(GoogleProject());
    }

    @Override
    public User getUser(String uid) {
        for(User user: users){
            if(Objects.equals(user.getUID(), uid)){
                return user;
            }
        }
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
    public User authenticate(String username, String password) throws Errors.AuthException {
        if(username.isEmpty() || password.isEmpty()){
            throw new Errors.AuthException("username or password missing", Errors.AuthError.MissingItems);
        }
        for(User user: users){
            if(Objects.equals(user.getEmail(), username) || Objects.equals(user.getUserName(), username)){
                if(Objects.equals(password, passwords.get(user))){
                    return user;
                }
                throw new Errors.AuthException("username or password missing", Errors.AuthError.InvalidUsername);
            }
        }
        throw new Errors.AuthException("username or password missing", Errors.AuthError.InvalidUsername);
    }

    @Override
    public boolean tryAuthenticate(String username, String password) {
        try {
            authenticate(username, password);
            return true;
        } catch (Errors.AuthException exception){
            return false;
        }
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
    public void register(User user, String password) throws Errors.RegisterException {
        if(user.getUID().isEmpty() || user.getUserName().isEmpty() || user.getName().isEmpty() || user.getEmail().isEmpty() || password.isEmpty()){
            throw new Errors.RegisterException("Item is missing", Errors.RegisterError.MissingItem);
        }
        if(!verifyUsername(user.getUserName())){
            throw new Errors.RegisterException("Username already in use", Errors.RegisterError.UsernameInUse);
        }
        if(!verifyEmail(user.getEmail())){
            throw new Errors.RegisterException("Account already exists", Errors.RegisterError.AccountAlreadyExists);
        }
        if(!verifyPassword(password)){
            throw new Errors.RegisterException("Invalid password", Errors.RegisterError.InvalidPassword);
        }
        users.add(user);
        passwords.put(user, password);
    }

    private boolean verifyUsername(String username) {
        for(User user: users){
            if(user.getUserName().equalsIgnoreCase(username)){
                return false;
            }
        }
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
    public boolean tryRegister(User user, String password) {
        try{
            register(user, password);
            return true;
        } catch (Errors.RegisterException exception){
            return false;
        }
    }

    @Override
    public void createProject(Project project) throws Errors.CreateProjectException {
        if(project == null || project.getOwner() == null || project.getStartDate() == null || project.getEndDate() == null){
            throw new Errors.CreateProjectException("Missing data", Errors.CreateProjectError.MissingItems);
        }
        if(project.getUID().isEmpty() || project.getTitle().isEmpty() || project.getDescription().isEmpty() ||
                project.getDescription().isEmpty() || project.getEndDate().before(project.getStartDate())){
            throw new Errors.CreateProjectException("Invalid data", Errors.CreateProjectError.InvalidProject);
        }
        projects.add(project);
        Log.e("Project", "Project Added: " + projects.size() +" items");
    }

    private Project GoogleProject(){
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 10);
        return new Project(
                random.nextInt(100000) + "",
                null,
                "Smart Cars",
                "https://pmcvariety.files.wordpress.com/2015/08/google-placeholder-logo.jpg?w=1000&h=563&crop=1",
                new String[]{ "Programmer", "Product Manager", "Experience Designer" },
                "The project focuses on building a self driving car, in which you are required to know Machine Learning and Artificial Intelligence Algorithms in order to be eligible for this project",
                "Mountain View, California, United States",
                Calendar.getInstance().getTime(),
                endDate.getTime());
    }

    private Project MicrosoftProject(){
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 18);

        Calendar startDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 8);

        return new Project(
                random.nextInt(100000) + "",
                null,
                "Cortana Search",
                "https://mspoweruser.com/wp-content/uploads/2016/09/Webgroesse_HighRes_Microsoft12711.jpg",
                new String[]{ "Programmer", "Program Manager", "Tester" },
                "This project focuses on implementing a Natural Language search for Cortana, for this we require that you have knowledge and background on Natural Language Processing or Artificial Intelligence",
                "Redmond, Washington, United States",
                startDate.getTime(),
                endDate.getTime());
    }
}
