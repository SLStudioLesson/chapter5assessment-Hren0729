package com.taskapp.dataaccess;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskDataAccess {

    private final String filePath;
    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除しないでください。
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // ヘッダー行をスキップ
            while ((line = br.readLine()) != null) {
                // 行のデータを分割
                String[] data = line.split(",");
    
                // カラム数を確認
                if (data.length != 4) {
                    System.err.println("Skipping invalid line (unexpected column count): " + line);
                    continue;
                }
    
                // 各カラムの妥当性を確認
                if (!isNumeric(data[0]) || !isNumeric(data[2]) || !isNumeric(data[3])) {
                    System.err.println("Skipping invalid line (parsing error): " + line);
                    continue;
                }
    
                try {
                    int code = Integer.parseInt(data[0]);
                    String name = data[1];
                    int status = Integer.parseInt(data[2]);
                    int userCode = Integer.parseInt(data[3]);
    
                    // Userオブジェクトを取得
                    User repUser = userDataAccess.findByCode(userCode);
                    if (repUser == null) {
                        System.err.println("Skipping invalid line (user not found): " + line);
                        continue;
                    }
    
                    // タスクオブジェクトを作成してリストに追加
                    tasks.add(new Task(code, name, status, repUser));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid line (unexpected number format): " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("タスクデータの読み込み中にエラーが発生しました: " + e.getMessage());
        }
        return tasks;
    }


    /**
     * タスクをCSVに保存します。
     *
     * @param task 保存するタスク
     */
    public void save(Task task) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
            pw.println(createLine(task));
        } catch (IOException e) {
            System.err.println("タスクデータの保存中にエラーが発生しました: " + e.getMessage());
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     *
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) {
        List<Task> tasks = findAll(); // 全タスクを取得
        for (Task task : tasks) {
            if (task.getCode() == code) {
                return task; // コードが一致したタスクを返す
            }
        }
        return null; // 見つからなければnullを返す
    }

    /**
     * タスクデータを更新します。
     *
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
        List<Task> tasks = findAll(); // すべてのタスクを取得
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                if (task.getCode() == updateTask.getCode()) {
                    pw.println(createLine(updateTask)); // 更新データ
                } else {
                    pw.println(createLine(task)); // 変更なし
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     *
     * @param code 削除するタスクのコード
     */
    // public void delete(int code) {
    // }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     *
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    private String createLine(Task task) {
        return String.format("%d,%s,%d,%d", task.getCode(), task.getName(), task.getStatus(), task.getRepUser().getCode());
    }

    /**
     * 文字列が数値であるかを判定します。
     *
     * @param input チェック対象の文字列
     * @return 数値ならtrue、それ以外はfalse
     */
    private boolean isNumeric(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
