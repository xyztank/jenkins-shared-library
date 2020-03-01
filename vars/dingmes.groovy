package org.devops

def getChangeString() {
    def changeString = ""
    def MAX_MSG_LEN = 20
    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            truncatedMsg = entry.msg.take(MAX_MSG_LEN)
            commitTime = new Date(entry.timestamp).format("yyyy-MM-dd HH:mm:ss")
            changeString += " - ${truncatedMsg} [${entry.author} ${commitTime}]\n"
        }
    }
    if (!changeString) {
        changeString = " - No new changes"
    }
    return (changeString)
}

def HttpReq(AppName,ImageTag=' ',Status,CatchInfo=' '){
    wrap([$class: 'BuildUser']){
        def DingTalkHook = "https://oapi.dingtalk.com/robot/send?access_token=67449753547bfcb8e2ee6088fdebaf2cdc7228787201fca83406fc449ffaf92"
        def ChangeLog = getChangeString()
        def ReqBody = """{
            "msgtype": "markdown",
            "markdown": {
                "title": "项目构建信息",
                "text": "### 构建信息\n>- 应用名称: **${AppName}**\n- 构建结果: **${Status} ${CatchInfo}**\n- 当前版本: **${ImageTag}**\n- 构建发起: **${env.BUILD_USER}**\n- 持续时间: **${currentBuild.durationString}**\n- 构建日志: [点击查看详情](${env.BUILD_URL}console)\n#### 更新记录: \n${ChangeLog}"
            },
            "at": {
                "atMobiles": [
                    "155xxxx5533"
                ], 
                "isAtAll": false
                }
            }"""
        // println(currentBuild.description)
        // println(currentBuild.changeSets)
        httpRequest acceptType: 'APPLICATION_JSON_UTF8',
                consoleLogResponseBody: false,
                contentType: 'APPLICATION_JSON_UTF8',
                httpMode: 'POST',
                ignoreSslErrors: true,
                requestBody: ReqBody,
                responseHandle: 'NONE',
                url: "${DingTalkHook}",
                quiet: true
    }
}