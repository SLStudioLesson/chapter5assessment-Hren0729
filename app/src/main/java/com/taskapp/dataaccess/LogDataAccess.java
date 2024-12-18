package com.taskapp.dataaccess;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.model.Log;

public class LogDataAccess {
    private final String filePath;

    public LogDataAccess() {
        filePath = "app/src/main/resources/logs.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     *
     * @param filePath ログファイルのパス
     */
    public LogDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * ログをCSVファイルに保存します。
     *
     * @param log 保存するログ
     */
    public void save(Log log) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
            pw.println(createLine(log));
        } catch (IOException e) {
            System.err.println("ログデータの保存中にエラーが発生しました: " + e.getMessage());
        }
    }

    /**
     * すべてのログを取得します。
     *
     * @return すべてのログのリスト
     */
    public List<Log> findAll() {
        List<Log> logs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // ヘッダー行の読み飛ばし
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length != 4) {
                    System.err.println("Skipping invalid line (unexpected column count): " + line);
                    continue;
                }

                try {
                    int taskCode = Integer.parseInt(data[0]);
                    int userCode = Integer.parseInt(data[1]);
                    int status = Integer.parseInt(data[2]);
                    LocalDate changeDate = LocalDate.parse(data[3]); // 変更日付は文字列として保持

                    logs.add(new Log(taskCode, userCode, status, changeDate));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid line (parsing error): " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("ログデータの読み込み中にエラーが発生しました: " + e.getMessage());
        }
        return logs;
    }

    /**
     * 指定したタスクコードに該当するログを削除します。
     *
     * @param taskCode 削除するログのタスクコード
     */
    public void deleteByTaskCode(int taskCode) {
        List<Log> logs = findAll();

        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("taskCode,userCode,status,changeDate");
            for (Log log : logs) {
                if (log.getTaskCode() != taskCode) {
                    pw.println(createLine(log)); 
                }
            }
        } catch (IOException e) {
            System.err.println("ログデータの削除中にエラーが発生しました: " + e.getMessage());
        }
    }

    /**
     * ログをCSVファイルに書き込むためのフォーマットを作成します。
     *
     * @param log フォーマットを作成するログ
     * @return CSVファイルに書き込むためのフォーマット
     */
    private String createLine(Log log) {
        return String.format("%d,%d,%d,%s", log.getTaskCode(), log.getChangeUserCode(), log.getStatus(), log.getChangeDate());
    }
}
