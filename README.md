# javaFx-binary-tree-visualization
tried to visualize various operations that can be performed on binary tree like - adding nodes, deleting nodes, BFS, DFS

environment-
java 12.0.1
javafx 12.0.1

to run in command prompt-
1. open command prompt in the directory in which the sample folder is located.
2. find where is javafx on your system. i have placed it at 'E:\javafx'.
3. navigate to '../javafx-sdk-12.0.1/lib'.
4. copy your current location data. for me it is 'E:\javafx\javafx-sdk-12.0.1\lib'.
5. type the following command in command prompt to compile the files-
    javac --module-path <paste here the location data you copied in step 4> --add-modules javafx.controls,javafx.fxml sample/Main.java
6. type the following command to see the code working-
    java --module-path E:\javafx\javafx-sdk-12.0.1\lib --add-modules javafx.controls,javafx.fxml sample.Main
    
