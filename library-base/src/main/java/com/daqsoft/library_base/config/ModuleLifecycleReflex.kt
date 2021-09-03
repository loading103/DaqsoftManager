package com.daqsoft.library_base.config

class ModuleLifecycleReflex {
    companion object{
        // 基础库
        private const val baseInit = "com.daqsoft.library_base.BaseModuleInit"

        // 主模块
        private const val mainInit = "com.daqsoft.module_main.MainModuleInit"

        // 任务模块
        private const val taskInit = "com.daqsoft.module_task.TaskModuleInit"

        // 项目模块
        private const val projectInit = "com.daqsoft.module_project.ProjectModuleInit"

        // 工作台模块
        private const val workbenchInit = "com.daqsoft.module_workbench.WorkbenchModuleInit"

        // 用户模块
        private const val mineInit = "com.daqsoft.module_mine.MineModuleInit"

        // webView模块
        private const val webViewInit = "com.daqsoft.module_webview.WebViewModuleInit"


        val initModuleNames = arrayOf<String>(
            baseInit,
            mainInit,
            taskInit,
            projectInit,
            workbenchInit,
            mineInit,
            webViewInit
        )
    }
}
