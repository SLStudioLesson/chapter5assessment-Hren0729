package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taskapp.exception.AppException;
import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.User;

public class TaskUI {
    private final BufferedReader reader;
    private final UserLogic userLogic;
    private final TaskLogic taskLogic;
    private User loginUser;

    public TaskUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        userLogic = new UserLogic();
        taskLogic = new TaskLogic();
    }

    public TaskUI(BufferedReader reader, UserLogic userLogic, TaskLogic taskLogic) {
        this.reader = reader;
        this.userLogic = userLogic;
        this.taskLogic = taskLogic;
    }

    public void displayMenu() {
        System.out.println("タスク管理アプリケーションにようこそ!!");
        boolean flg = true;

        while (flg) {
            try {
                System.out.println("以下1~3のメニューから好きな選択肢を選んでください。");
                System.out.println("1. タスク一覧, 2. タスク新規登録, 3. ログアウト");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();
                System.out.println();

                switch (selectMenu) {
                    case "1":
                        taskLogic.showAll(loginUser);
                        break;
                    case "2":
                        inputNewInformation();
                        break;
                    case "3":
                        System.out.println("ログアウトしました。");
                        flg = false;
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    public void inputLogin() {
        boolean flg = true;

        while (flg) {
            try {
                System.out.print("メールアドレスを入力してください：");
                String email = reader.readLine();

                System.out.print("パスワードを入力してください：");
                String password = reader.readLine();

                // ログイン処理を呼び出す
                loginUser = userLogic.login(email, password);
                System.out.println("ユーザー名：" + loginUser.getName() + "でログインしました。");
                flg = false; // ログイン成功したらフラグを変更
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    public void inputNewInformation() {
        try {
            System.out.print("新しいタスク名を入力してください：");
            String taskName = reader.readLine();

            System.out.print("期限日を入力してください（例: 2024-12-31）：");
            String dueDate = reader.readLine();

            System.out.print("優先度を入力してください（1: 高, 2: 中, 3: 低）：");
            String priorityInput = reader.readLine();

            if (!isNumeric(priorityInput)) {
                System.out.println("優先度は数値（1, 2, 3）で入力してください。");
                return;
            }
            int priority = Integer.parseInt(priorityInput);

            taskLogic.save(taskName, dueDate, priority, loginUser);
            System.out.println("新しいタスクを登録しました！");
        } catch (IOException e) {
            System.out.println("入力エラーが発生しました。再度お試しください。");
        }
    }

    public boolean isNumeric(String inputText) {
        if (inputText == null || inputText.isEmpty()) {
            return false;
        }
        try {
            int num = Integer.parseInt(inputText);
            return num > 0; // 負の数は無効
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        TaskUI taskUI = new TaskUI();
        taskUI.inputLogin(); // 最初にログインを促す
        taskUI.displayMenu(); // メニューを表示
    }
}
