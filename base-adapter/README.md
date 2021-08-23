适配程序没有打包会有"未授权"的悬浮图标提示。

项目打包apk使用android studio的Build/Generate Signed Bundle/APK，
用 http://gitlab.txzing.com/dev/support/android/base-adapter （公版适配）的打包配置，
具体位置是 TXZAdapter2.0/build.gradle，版本使用同级目录下的 TXZAdapter2.0/signature/txz.keystore签名。

versionCode存储在TXZAdapter2.0/version.properties文件下，每次项目打包版本都会自增1，从1开始。
例如：默认version.properties文件的值是1，打包时候自增1，结果就是2。

versionName =【打包日期】+ versionCode
例如：200215-2
编译出来的apk名称：TXZAdapter2.0-versionName-release。
例如：TXZAdapter2.0-200215-2-release.apk