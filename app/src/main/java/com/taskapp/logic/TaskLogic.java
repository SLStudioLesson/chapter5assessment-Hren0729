package com.taskapp.logic;

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

    /**
     * 全てのタスクを表示します。
     *
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        System.out.println("タスク一覧：");
        taskDataAccess.findAll()
            .stream()
            .filter(task -> task.getCode() == loginUser.getCode())
            .forEach(task -> System.out.println(task));
    }

    /**
     * 新しいタスクを保存します。
     *
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode, User loginUser) throws AppException {
        // 担当ユーザーコードが有効かを確認
        if (userDataAccess.findByCode(repUserCode) == null) {
            throw new AppException("担当ユーザーコードが存在しません。");
        }

        // タスクを生成して保存
        Task newTask = new Task(code, name, repUserCode, 1, loginUser.getCode()); // 初期ステータスは1
        taskDataAccess.save(newTask);

        // ログ記録
        logDataAccess.save(new Log(code, "タスクを登録しました", loginUser.getCode()));
        System.out.println("タスクを登録しました。");
    }

    /**
     * タスクのステータスを変更します。
     *
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status, User loginUser) throws AppException {
        Task task = taskDataAccess.findByCode(code);
        if (task == null) {
            throw new AppException("タスクコードが存在しません。");
        }

        // ステータスの変更条件を確認（前のステータスより1つ先であること）
        if (status != task.getStatus() + 1) {
            throw new AppException("ステータスは前のステータスより1つ先でなければなりません。");
        }

        // ステータスを更新
        task.setStatus(status);
        taskDataAccess.update(task);

        // ログ記録
        logDataAccess.save(new Log(code, "タスクのステータスが変更されました", loginUser.getCode()));
        System.out.println("タスクのステータスが変更されました。");
    }

    /**
     * タスクを削除します。
     *
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    public void delete(int code) throws AppException {
        Task task = taskDataAccess.findByCode(code);
        if (task == null) {
            throw new AppException("タスクコードが存在しません。");
        }
    
        if (task.getStatus() != 3) {
            throw new AppException("タスクが完了していないため削除できません。");
        }
    
        taskDataAccess.delete(code);
        logDataAccess.deleteByTaskCode(code);
        System.out.println("タスクを削除しました。");
    }
}
