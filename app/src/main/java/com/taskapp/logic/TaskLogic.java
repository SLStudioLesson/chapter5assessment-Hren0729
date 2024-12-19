package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;

    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    public void showAll(User loginUser) {
        List<Task> tasks = taskDataAccess.findAll();
        tasks.stream()
            .forEach(task -> {
                User responsibleUser = task.getRepUser();
                String responsibleName = (responsibleUser.getCode() == loginUser.getCode()) ?
                    "あなたが担当しています" : responsibleUser.getName() + "が担当しています";

                String status = getStatusString(task.getStatus());

                System.out.printf("%d. タスク名：%s, 担当者名：%s, ステータス：%s%n",
                    task.getCode(),
                    task.getName(),
                    responsibleName,
                    status);
            });
    }

    private String getStatusString(int status) {
        switch (status) {
            case 0: return "未着手";
            case 1: return "着手中";
            case 2: return "完了";
            default: return "不明なステータス";
        }
    }

    public void save(int code, String name, int repUserCode, User loginUser) throws AppException {
        User responsibleUser = userDataAccess.findByCode(repUserCode);
        if (responsibleUser == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }

        Task task = new Task(code, name, 0, responsibleUser);
        taskDataAccess.save(task);

        Log log = new Log(code, loginUser.getCode(), 0, LocalDate.now());
        logDataAccess.save(log);
    }

    public void updateTaskName(int taskCode, String newTaskName, User loginUser) throws AppException {
        Task task = taskDataAccess.findByCode(taskCode);
        if (task == null) {
            throw new AppException("指定されたタスクが存在しません");
        }

        if (task.getRepUser().getCode() != loginUser.getCode()) {
            throw new AppException("このタスクの更新権限がありません");
        }

        task.setName(newTaskName);
        taskDataAccess.update(task);

        Log log = new Log(taskCode, loginUser.getCode(), task.getStatus(), LocalDate.now());
        logDataAccess.save(log);

        System.out.println("タスク名が更新されました：" + newTaskName);
    }

    public void changeStatus(int code, int status, User loginUser) throws AppException {
        Task task = taskDataAccess.findByCode(code);
        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }
        int currentStatus = task.getStatus();

        if (status != currentStatus + 1) {
            throw new AppException("前のステータスより1つ先のもののみを選択してください");
        }

        task.setStatus(status);
        taskDataAccess.update(task);

        Log log = new Log(code, loginUser.getCode(), status, LocalDate.now());
        logDataAccess.save(log);

        System.out.println("タスクのステータスが変更されました");
    }

    // public void delete(int code) throws AppException {

    // }
}
