//package com.ewertonilima.dynamodb.entity
//
//import org.springframework.boot.CommandLineRunner
//import org.springframework.stereotype.Component
//import java.io.IOException
//import java.nio.file.Files
//import java.nio.file.Paths
//
//
//@Component
//class ScriptRunner : CommandLineRunner {
//    @Throws(Exception::class)
//    override fun run(vararg args: String) {
//        executeScript()
//    }
//
//    @Throws(IOException::class, InterruptedException::class)
//    private fun executeScript() {
//        // Path to your script file
//        val scriptFilePath = "src/main/resources/scripts/up-localstack.sh"
//
//        // Check if the script file exists
//        val scriptPath = Paths.get(scriptFilePath)
//        if (Files.exists(scriptPath)) {
//            // Execute the script using ProcessBuilder
//            val processBuilder = ProcessBuilder(scriptFilePath)
//            val process = processBuilder.start()
//
//            // Wait for the script execution to complete
//            val exitCode = process.waitFor()
//            if (exitCode == 0) {
//                println("Script executed successfully.")
//            } else {
//                System.err.println("Error executing script. Exit code: $exitCode")
//            }
//        } else {
//            System.err.println("Script file not found: $scriptFilePath")
//        }
//    }
//}
//
