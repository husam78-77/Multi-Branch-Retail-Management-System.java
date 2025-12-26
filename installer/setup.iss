[Setup]
AppName=MultiBranch Retail Management System
AppVersion=1.0
DefaultDirName={pf}\MultiBranchRetail
DefaultGroupName=MultiBranch Retail Management System
OutputDir=D:\husam\javaproject\installer
OutputBaseFilename=MultiBranchRetailManagementSystem_Setup
SetupIconFile=app\icon.ico
Compression=lzma
SolidCompression=yes

[Files]
Source: "app\*"; DestDir: "{app}"; Flags: recursesubdirs

[Icons]
Name: "{group}\MultiBranch Retail Management System"; \
Filename: "{app}\runtime\bin\java.exe"; \
Parameters: "-jar ""{app}\MultiBranchRetailManagementSystem-1.0-shaded.jar"""; \
IconFilename: "{app}\icon.ico"

Name: "{commondesktop}\MultiBranch Retail"; \
Filename: "{app}\runtime\bin\java.exe"; \
Parameters: "-jar ""{app}\MultiBranchRetailManagementSystem-1.0-shaded.jar"""; \
IconFilename: "{app}\icon.ico"

[Run]
Filename: "{app}\runtime\bin\java.exe"; \
Parameters: "-jar ""{app}\MultiBranchRetailManagementSystem-1.0-shaded.jar"""; \
Flags: nowait postinstall
