package controllers

import javafx.beans.Observable
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.stage.FileChooser
import java.io.File
import javafx.scene.control.TableView
import RecordData
import javafx.collections.FXCollections
import javafx.scene.control.*
import java.util.*
import javax.swing.text.LabelView
import kotlin.concurrent.timer
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.javafx.JavaFx as UI

class MainFormController {
    @FXML
    lateinit var LoadFileButton: Button
    lateinit var checkSelected: Button
    lateinit var data:ObservableList<RecordData>
    lateinit var listTable:TableView<RecordData>
    lateinit var statusLabel: Label
    lateinit var progressBar:ProgressBar


    fun initialize() {
        //print("Controller working")

        listTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        LoadFileButton.setOnAction {
            val fc = FileChooser()
            val file= fc.showOpenDialog(null)
            if(file!=null) {
                LoadFileButton.isDisable=true
                statusLabel.text="Processing "+file.absolutePath
                progressBar.progress= 0.0
                /*var lft = LoadFileThread(file,progressBar)
                lft.apply { println("Apply works here") }
                lft.start()*/
                launch(CommonPool){
                    var loadeddata=LoadFile(file,progressBar)
                    launch(UI) {
                        LoadFileButton.isDisable=false
                        progressBar.progress = 0.0
                        statusLabel.text = ""
                        data=FXCollections.observableList(loadeddata)
                        listTable.items=data

                    }
                }
            }
        }

        checkSelected.setOnAction {
            var sel_list:List<RecordData>
            if(listTable.selectionModel.selectedItems.count()>0) sel_list= listTable.selectionModel.selectedItems.filter { it.Scanned == false }
            else sel_list=listTable.items.filter { it.Scanned == false }
            if(sel_list.count()>0) {
                var pos=0.0
                launch(CommonPool) {

                }
            }
        }


    }



    suspend fun LoadFile(fl:File,pb:ProgressBar):MutableList<RecordData>{
        var pos=0.0
        var lng=0
        var current_data= mutableListOf<RecordData>()
        if(fl!=null) {
            fl.forEachLine { lng++ }
            if (lng > 0) {
                fl.forEachLine {
                    var cur_rec = RecordData(SourceFileName = fl.absolutePath, Results = emptyList())
                    var fragments = it.split(Regex("\\s+"))
                    fragments.forEach {
                        cur_rec.AddData(it)
                    }
                    current_data.add(cur_rec)
                    pos += 1
                    pb.progress = pos / lng

                }
            }
        }
        return current_data
    }

}
