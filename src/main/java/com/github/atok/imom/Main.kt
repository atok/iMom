package com.github.atok.imom

import com.github.atok.imom.repository.Storage
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory
import com.sun.javafx.application.HostServicesDelegate
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Main : Application() {

    val storage = Storage()

    init {
        Main.hostServices = HostServicesFactory.getInstance(this)
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "iMom"

        val loader = FXMLLoader(this.javaClass.getResource("/main2.fxml"))
        val root = loader.load<Parent>()
        val controller = loader.getController<MainController>()

        primaryStage.scene = Scene(root, 1000.0, 700.0)
        primaryStage.show()

        storage.load()
        storage.save()

        controller.init(primaryStage, storage)
        controller.show("")
    }

    companion object {

        lateinit var hostServices: HostServicesDelegate

        @JvmStatic fun main(args: Array<String>) {
            System.setProperty("prism.lcdtext", "true");
            System.setProperty("prism.text", "t2k");

            Application.launch(Main::class.java, *args)
        }
    }
}
