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

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
    // 全てのタスクを取得
    List<Task> tasks = taskDataAccess.findAll();

    // タスクをストリームで処理
    tasks.stream()
        .forEach(task -> {
            // 担当者の取得
            User responsibleUser = userDataAccess.findByCode(task.getRepUser());

            // 担当者名を判定
            String responsibleName = (responsibleUser != null) ? 
                (responsibleUser.getCode() == loginUser.getCode()) ? 
                "あなたが担当しています" : responsibleUser.getName() + "が担当しています" 
                : "不明";

            // ステータスの数値を文字列に変換
            String status = getStatusString(task.getStatus());

            // タスク情報を表示
            System.out.printf("%d. タスク名：%s, 担当者名：%s, ステータス：%s%n",
                        task.getCode(),
                        task.getName(),
                        responsibleName,
                        status);
        });
    }

    private String getStatusString(int status) {
        switch (status) {
            case 0:
                return "未着手";
            case 1:
                return "着手中";
            case 2:
                return "完了";
            default:
                return "不明なステータス";
        }
    }


    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
        User responsibleUser = userDataAccess.findByCode(repUserCode);
        if (responsibleUser == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }

        Task task = new Task(code, name, 0, repUserCode); // ステータスは初期値の0（未着手）
        taskDataAccess.save(task);

        Log log = new Log(code, loginUser.getCode(), 0, LocalDate.now());
        logDataAccess.save(log);
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void updateTaskName(int taskCode, String newTaskName, User loginUser) throws AppException {
        // タスクの取得
        Task task = taskDataAccess.findByCode(taskCode);
        if (task == null) {
            throw new AppException("指定されたタスクが存在しません");
        }

        // タスクの担当者がログインユーザーと一致するか確認
        if (task.getRepUser() != loginUser.getCode()) {
            throw new AppException("このタスクの更新権限がありません");
        }

        // タスク名の更新
        task.setName(newTaskName);
        taskDataAccess.update(task); // タスク情報の更新

        // 更新したことをログに保存
        Log log = new Log(taskCode, loginUser.getCode(), task.getStatus(), LocalDate.now());
        logDataAccess.save(log);

        System.out.println("タスク名が更新されました：" + newTaskName);
    }
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {
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

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    public void delete(int code) throws AppException {
        
    }
}