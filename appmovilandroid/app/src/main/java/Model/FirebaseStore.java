package Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class FirebaseStore implements IAsyncStore {

    private FirebaseFirestore database;
    private FirebaseAuth auth;

    FirebaseStore() {
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public Task<User> getUser(String uid) {
        return database.collection("users").document(uid).get().continueWith(AsyncTask.THREAD_POOL_EXECUTOR, new Continuation<DocumentSnapshot, User>() {
            @Override
            public User then(@NonNull Task<DocumentSnapshot> task) throws Exception {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    Map<String, Object> map = document.getData();
                    if(map.get("projectsMember") == null) {
                        map.put("projectsMember", new ArrayList<>());
                    } else {
                        Map<String, Object> projects = (Map<String, Object>) document.getData().get("projectsMember");
                        String[] keys = new String[projects.keySet().size()];
                        projects.keySet().toArray(keys);
                        map.put("projectsMember", new ArrayList<>(Arrays.asList(keys)));
                    }
                    User result = new User(map, document.getId());
                    return result;
                } else {
                    return null;
                }
            } else {
                return null;
            }
            }
        });
    }

    @Override
    public Task<ArrayList<User>> getUsers(final ArrayList<String> userUIDs) {
        final HashSet<String> ids = new HashSet<>(userUIDs);

        return database.collection("users").get().continueWith(AsyncTask.THREAD_POOL_EXECUTOR ,new Continuation<QuerySnapshot, ArrayList<User>>() {
            @Override
            public ArrayList<User> then(@NonNull Task<QuerySnapshot> task) throws Exception {
            if(task.isSuccessful()){
                QuerySnapshot documents = task.getResult();
                ArrayList<User> result = new ArrayList<>();
                for (DocumentSnapshot doc: documents){
                    if(ids.contains(doc.getId())) {
                        Map<String, Object> map = doc.getData();
                        if (map.get("projectsMember") == null) {
                            map.put("projectsMember", new ArrayList<>());
                        } else {
                            Map<String, Object> projects = (Map<String, Object>) doc.getData().get("projectsMember");
                            String[] keys = new String[projects.keySet().size()];
                            projects.keySet().toArray(keys);
                            map.put("projectsMember", new ArrayList<String>(Arrays.asList(keys)));
                        }
                        result.add(new User(map, doc.getId()));
                    }
                }
                return result;
            } else {
                throw task.getException();
            }
            }
        });
    }

    @Override
    public Task<ArrayList<String>> getAllProjectsUIDs() {
        return database.collection("projects").get().continueWith(AsyncTask.THREAD_POOL_EXECUTOR, new Continuation<QuerySnapshot, ArrayList<String>>() {
            @Override
            public ArrayList<String> then(@NonNull Task<QuerySnapshot> task) throws Exception {
            if(task.isSuccessful()){
                QuerySnapshot documents = task.getResult();
                ArrayList<String> result = new ArrayList<>();
                for (DocumentSnapshot doc: documents.getDocuments()) {
                    String current = doc.getId();
                    result.add(current);
                }
                return result;
            } else {
                throw task.getException();
            }
            }
        });
    }

    @Override
    public Task<Project> getProject(final String uid) {
        return database.collection("projects").document(uid).get().continueWith(AsyncTask.THREAD_POOL_EXECUTOR, new Continuation<DocumentSnapshot, Project>() {
            @Override
            public Project then(@NonNull Task<DocumentSnapshot> task) throws Exception {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                return new Project(document.getData(), document.getId());
            } else {
                throw task.getException();
            }
            }
        });

    }

    @Override
    public Task<ArrayList<Project>> getProjects(ArrayList<String> uids) {
        final HashSet<String> ids = new HashSet<>(uids);

        return database.collection("projects").get().continueWith(AsyncTask.THREAD_POOL_EXECUTOR, new Continuation<QuerySnapshot, ArrayList<Project>>() {
            @Override
            public ArrayList<Project> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if(task.isSuccessful()){
                    QuerySnapshot documents = task.getResult();
                    ArrayList<Project> result = new ArrayList<>();
                    for (DocumentSnapshot doc: documents.getDocuments()) {
                        if(ids.contains(doc.getId())) {
                            result.add(new Project(doc.getData(), doc.getId()));
                        }
                    }
                    return result;
                } else {
                    throw task.getException();
                }
            }
        });
    }

    @Override
    public Task<ArrayList<String>> getMemberedProjects(String uid) {
        return database.collection("users").document(uid).get()
        .continueWith(
            AsyncTask.THREAD_POOL_EXECUTOR,
            new Continuation<DocumentSnapshot, ArrayList<String>>() {
                @Override
                public ArrayList<String> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()) {
                            Map<String, Object> map = document.getData();
                            if (map.get("projectsMember") != null) {
                                Map<String, Boolean> projects = (Map<String, Boolean>) map.get("projectsMember");
                                return new ArrayList<>(projects.keySet());
                            }
                        } else {
                            throw task.getException();
                        }
                    }
                    return null;
                }

            }
        );

    }

    @Override
    public Task<Void> createProject(final Project project) throws Errors.CreateProjectException {

        return database.collection("projects").add(project.toMap()).continueWith(AsyncTask.THREAD_POOL_EXECUTOR, new Continuation<DocumentReference, String>() {

            @Override
            public String then(@NonNull Task<DocumentReference> task) throws Exception {
            if (task.isSuccessful()) {
                return task.getResult().getId();
            } else {
                throw task.getException();
            }
            }
        }).continueWithTask(new Continuation<String, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<String> task) throws Exception {
            if (task.isSuccessful()) {
                project.setUID(task.getResult());
                return updateProject(project);
            } else {
                throw task.getException();
            }
            }
        });
    }

    @Override
    public Task<User> authenticate(final String username, final String password) throws Errors.AuthException {
        return auth.signInWithEmailAndPassword(username, password).continueWith(AsyncTask.THREAD_POOL_EXECUTOR, new Continuation<AuthResult, String>() {
            @Override
            public String then(@NonNull Task<AuthResult> task) throws Exception {
            if(task.isSuccessful()){
                return task.getResult().getUser().getUid();
            } else {
                throw task.getException();
            }
            }
        }).continueWithTask(new Continuation<String, Task<User>>() {
            @Override
            public Task<User> then(@NonNull Task<String> task) throws Exception {
            if(task.isSuccessful()) {
                return getUser(task.getResult());
            } else {
                throw task.getException();
            }
            }
        });
    }

    @Override
    public Task<User> register(final User user, String password) throws Errors.RegisterException {
        return auth.createUserWithEmailAndPassword(user.getEmail(), password).continueWithTask(AsyncTask.THREAD_POOL_EXECUTOR, new Continuation<AuthResult, Task<User>>() {
            @Override
            public Task<User> then(@NonNull Task<AuthResult> task) throws Exception {
                if(task.isSuccessful()){
                    user.setUID(task.getResult().getUser().getUid());
                    return createUser(user);
                } else {
                    throw task.getException();
                }
            }
        });
    }

    @Override
    public Task<Void> resetPassword(String email) {
        return auth.sendPasswordResetEmail(email);
    }

    @Override
    public Task<Void> updateUser(final User user) {

        Map<String, Object> newUser = user.toMap();

        newUser.remove("projectsMember");
        newUser.remove("rating");

       return database.collection("users").document(user.getUID()).update(newUser);
    }

    @Override
    public Task<Void> updateProject(final Project project){
        final TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
        Task<String> upload = taskCompletionSource.getTask();

        if(project.getImageUrl() == null || project.getImageUrl().isEmpty()) {
            if(project.getImage() == null){
                String url = "https://firebasestorage.googleapis.com/v0/b/perfect-project-f5f66.appspot.com/o/images%2Fplaceholder-profile.jpg?alt=media&token=3f004413-394a-47de-b93f-b012bb895ea3";
                taskCompletionSource.setResult(url);
            }else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                project.getImage().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Uri url = null;
                try {
                    FirebaseStorage.getInstance().getReference().child("images/" + project.getUID() + ".jpg").putBytes(stream.toByteArray()).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<String>>() {
                        @Override
                        public Task<String> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(task.isSuccessful()){
                                taskCompletionSource.setResult(task.getResult().getDownloadUrl().toString());
                            }
                            else {
                                String url = "https://firebasestorage.googleapis.com/v0/b/perfect-project-f5f66.appspot.com/o/images%2Fplaceholder-profile.jpg?alt=media&token=5876e60f-1561-4431-8a0a-8fab5d4bd4cf";
                                taskCompletionSource.setResult(url);
                            }
                            return taskCompletionSource.getTask();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            taskCompletionSource.setResult(project.getImageUrl());
        }
        return upload.continueWithTask(AsyncTask.THREAD_POOL_EXECUTOR, new Continuation<String, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<String> task) throws Exception {
                if (task.isSuccessful()) {
                    project.setImageUrl(task.getResult());
                    return database.collection("projects").document(project.getUID()).set(project.toMap());
                } else {
                    throw task.getException();
                }
            }
        });
    }

    private Task<User> createUser(final User user){
        if(user.getProfileImageURL() == null || user.getProfileImageURL().isEmpty()) {
            if(user.getProfileImage() == null){
                user.setProfileImageURL("https://firebasestorage.googleapis.com/v0/b/perfect-project-f5f66.appspot.com/o/images%2Fplaceholder-profile.jpg?alt=media&token=5876e60f-1561-4431-8a0a-8fab5d4bd4cf");
            } else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                user.getProfileImage().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Uri url = null;
                try {
                    url = Tasks.await(FirebaseStorage.getInstance().getReference().child("images/profiles/" + user.getUID() + ".jpg").putBytes(stream.toByteArray())).getDownloadUrl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                user.setProfileImageURL(url.toString());
            }
        }

        Map<String, Object> userMap = user.toMap();

        if(userMap.get("projectsMember") != null && userMap.get("projectsMember") instanceof List){
            List<String> lista = (List<String>)userMap.get("projectsMember");
            if(lista.isEmpty()){
                userMap.put("projectsMember", null);
            }
            else {
                Map<String, Object> memberMap = new HashMap<>();
                for (String uid: lista) {
                    memberMap.put("uid", true);
                }
                userMap.put("projectsMember", user);
            }
        }
        return database.collection("users").document(user.getUID()).set(userMap, SetOptions.merge()).continueWith(AsyncTask.THREAD_POOL_EXECUTOR, new Continuation<Void, User>() {
            @Override
            public User then(@NonNull Task<Void> task) throws Exception {
                if(task.isSuccessful()){
                    return user;
                } else {
                    throw task.getException();
                }
            }
        });
    }
  
    @Override
    public Task<Void> deleteProject(Project project) {
        return database.collection("projects").document(project.getUID()).delete();
    }

    @Override
    public Task<Void> rateUser(String uid, double rating) {
        return database.collection("users").document(uid).update("rating", rating);
    }

    public Task<Double> getRating(String userUID){
        return database.collection("users").document(userUID).get()
        .continueWith(
                AsyncTask.THREAD_POOL_EXECUTOR,
                new Continuation<DocumentSnapshot, Double>() {
                    @Override
                    public Double then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                Map<String, Object> map = document.getData();
                                if (map.get("rating") != null) {
                                    return (Double)map.get("rating");
                                }
                            } else {
                                throw task.getException();
                            }
                        }
                        return null;
                    }
                }
        );

    }


    @Override
    public Task<Void> removeProjectOwned(User currentUser, final String uid) {
        final DocumentReference reference = database.collection("users").document(currentUser.getUID());
        return reference.get().continueWithTask(new Continuation<DocumentSnapshot, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
            if(task.isSuccessful()){
                Map<String, Object> data = task.getResult().getData();
                data.remove(uid);
                return reference.update("projectsOwned", data.get("projectsOwned"));
            } else {
                throw task.getException();
            }
            }
        });
    }

    public Task<Void> addProjectMember(String userUID, String projectUID){
        return database.collection("users").document(userUID).update("projectsMember." + projectUID, true);
    }

    public Task<Void> removeProjectMember(String userUID, String projectUID){
        return database.collection("users").document(userUID).update("projectsMember." + projectUID, false);
    }

}
