package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.model.Task;

public class TaskDataAccess {

    private final String filePath;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
    }

    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    int code = Integer.parseInt(data[0]);
                    String name = data[1];
                    int status = Integer.parseInt(data[2]);
                    int repUserCode = Integer.parseInt(data[3]);
                    tasks.add(new Task(code, name, status, repUserCode));
                }
            }
        } catch (IOException e) {
            System.out.println("CSVファイルの読み込み中にエラーが発生しました: " + e.getMessage());
        }
        return tasks;
    }

    public void save(Task task) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(createLine(task));
            bw.newLine();
        } catch (IOException e) {
            System.out.println("CSVファイルへの書き込み中にエラーが発生しました: " + e.getMessage());
        }
    }

    public Task findByCode(int code) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4 && Integer.parseInt(data[0]) == code) {
                    String name = data[1];
                    int status = Integer.parseInt(data[2]);
                    int repUserCode = Integer.parseInt(data[3]);
                    return new Task(code, name, status, repUserCode);
                }
            }
        } catch (IOException e) {
            System.out.println("CSVファイルの読み込み中にエラーが発生しました: " + e.getMessage());
        }
        return null;
    }

    public void update(Task updateTask) {
        List<Task> tasks = findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                if (task.getCode() == updateTask.getCode()) {
                    bw.write(createLine(updateTask));
                } else {
                    bw.write(createLine(task));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("CSVファイルの更新中にエラーが発生しました: " + e.getMessage());
        }
    }

    public void delete(int code) {
        List<Task> tasks = findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                if (task.getCode() != code) {
                    bw.write(createLine(task));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("CSVファイルの削除中にエラーが発生しました: " + e.getMessage());
        }
    }

    private String createLine(Task task) {
        return task.getCode() + "," + task.getName() + "," + task.getStatus() + "," + task.getRepUserCode();
    }
}
