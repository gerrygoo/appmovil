package Model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by ianne on 2/04/2018.
 */

public class Model implements IModel {

    private final IAsyncStore Store;
    private static Model instance;
    private User currentUser;

    private Model(IAsyncStore store){
        Store = store;
    }

    public static Model getInstance() {
        if(instance == null){
            instance = new Model(new FirebaseStore());
        }
        return instance;
    }

    @Override
    public Task<ArrayList<Project>> getAvailableProjects() {
        return Store.getAllProjectsUIDs().continueWithTask(new Continuation<ArrayList<String>, Task<ArrayList<Project>>>() {
            @Override
            public Task<ArrayList<Project>> then(@NonNull Task<ArrayList<String>> task) throws Exception {
                if (task.isSuccessful()) {
                    ArrayList<String> uids = task.getResult();
                    ArrayList<String> pendingProjects = new ArrayList<>(uids.size());
                    for (String uid : uids) {
                        if (!currentUser.hasReviewedProject(uid)) {
                            pendingProjects.add(uid);
                        }
                    }
                    return Store.getProjects(pendingProjects);
                } else {
                    throw task.getException();
                }
            }
        });
    }

    @Override
    public Task<ArrayList<Project>> getMyProjects() {
        return Store.getProjects(currentUser.getProjectsMember());
    }

    @Override
    public void viewNotification(Project project) {
        currentUser.viewNotification(project);
        Store.updateUser(currentUser);
    }

    @Override
    public Task<ArrayList<User>> getApplicants(final String projectUID) {
        final TaskCompletionSource<ArrayList<User>> taskCompletionSource = new TaskCompletionSource<>();

        Store.getProject(projectUID).addOnCompleteListener(new OnCompleteListener<Project>() {
            @Override
            public void onComplete(@NonNull Task<Project> task) {
                if(task.isSuccessful()){
                    try {
                        ArrayList<String> ids = Tasks.await(Store.getProject(projectUID)).getApplicants();
                        Tasks.await(Store.getUsers(ids));
                    } catch (ExecutionException | InterruptedException e) {
                        taskCompletionSource.setException(e);
                    }
                } else {
                    taskCompletionSource.setException(task.getException());
                }
            }
        });
        return taskCompletionSource.getTask();
    }

    @Override
    public Task<ArrayList<User>> getTeam(final String projectUID) {
        return Store.getProject(projectUID).continueWith(new Continuation<Project, ArrayList<User>>() {
            @Override
            public ArrayList<User> then(@NonNull Task<Project> task) throws Exception {
                if(task.isSuccessful()){
                    try {
                        ArrayList<String> ids = Tasks.await(Store.getProject(projectUID)).getTeam();
                        return Tasks.await(Store.getUsers(ids));
                    } catch (ExecutionException | InterruptedException e) {
                        throw e;
                    }
                } else {
                    throw task.getException();
                }
            }
        });
    }

    @Override
    public Task<Boolean> authenticate(final String username, final String password) throws Errors.AuthException {

        if(username.isEmpty() || password.isEmpty()){
            throw new Errors.AuthException("Username or password is Missing", Errors.AuthError.MissingItems);
        }
        return Store.authenticate(username, password).continueWith(new Continuation<User, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<User> task) throws Exception {
                if(task.isSuccessful()){
                    currentUser = task.getResult();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void logout() {
        this.currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    // TODO
    @Override
    public void register(User user, String password) throws Errors.RegisterException {
        if(user == null || password.isEmpty()){
            throw new Errors.RegisterException("Missing user or password", Errors.RegisterError.MissingItem);
        }
        Store.register(user,password);
    }

    @Override
    public Task<ArrayList<Project>> getOwnedProjects() {
        return Store.getProjects(currentUser.getProjectsOwned());
    }

    @Override
    public Task<HashMap<Project, Boolean>> getNotifications() {
        ArrayList<String> notificationsIds = new ArrayList<>();
        for (String key: currentUser.getNotifications().keySet()) {
            notificationsIds.add(key);
        }

        return Store.getProjects(notificationsIds).continueWith(new Continuation<ArrayList<Project>, HashMap<Project, Boolean>>() {
            @Override
            public HashMap<Project, Boolean> then(@NonNull Task<ArrayList<Project>> task) throws Exception {
                if(task.isSuccessful()){
                    HashMap<Project, Boolean> notifications = new HashMap<>();

                    ArrayList<Project> projects = task.getResult();
                    for (Project p: projects) {
                        notifications.put(p, currentUser.getNotifications().get(p.getUID()));
                    }
                    return notifications;
                } else {
                    throw task.getException();
                }
            }
        });
    }

    @Override
    public void reviewApplicant(Project project, User applicant, boolean accept) {
        if(accept) {
            project.addTeamMember(applicant);
            applicant.addProject(project);
            Store.updateUser(applicant);
        }
        project.removeApplicant(applicant);
        Store.updateProject(project);
    }

    @Override
    public void createProject(Project project) throws Errors.CreateProjectException {
        Store.createProject(project);
        currentUser.addProjectOwned(project);
        Store.updateUser(currentUser);
    }

    @Override
    public void reviewProject(Project project, boolean accept) {
        currentUser.reviewProject(project, accept);
        Store.updateUser(currentUser);
        project.addApplicant(currentUser);
        Store.updateProject(project);
    }
}
