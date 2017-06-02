
containerIndex=WScript.Arguments(0)
teamCount=WScript.Arguments(1)
computerCount=WScript.Arguments(2)
lapCount=WScript.Arguments(3)

agentParams=""
If containerIndex= 1 Then
	For i = 1 To teamCount Step 1
		agentParams=agentParams& "Runner" & containerIndex & "Team" & i & ":RAAgent(" & containerIndex & "," & i & "," & computerCount & "," & lapCount &");"
		agentParams=agentParams& "Runner" & (CInt(containerIndex)+CInt(computerCount)) & "Team" & i & ":RAAgent(" & (CInt(containerIndex)+CInt(computerCount)) & "," & i & "," & computerCount & "," & lapCount &");"
	Next
	Set WshShell = CreateObject("WScript.Shell")
	WshShell.Run "java -cp jade.jar;commons-codec-1.3.jar;classes jade.Boot -container -agents """ & agentParams & """ -host localhost -port 1111"
Else
	For i = 1 To teamCount Step 1
		agentParams=agentParams& "Runner" & containerIndex & "Team" & i & ":RAAgent(" & containerIndex & "," & i & "," & computerCount & "," & lapCount &");"
	Next
	Set WshShell = CreateObject("WScript.Shell")
	WshShell.Run "java -cp jade.jar;commons-codec-1.3.jar;classes jade.Boot -container -agents """ & agentParams & """ -host localhost -port 1111"
End If