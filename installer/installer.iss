; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Odyssey Beta"
#define MyAppVersion "1.3.0"
#define MyAppPublisher "Bearable Games"
#define MyAppURL "http://bearable.games"
#define MyAppExeName "Odyssey.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application. Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{C037210D-081F-4A5D-A4C6-059551AACC3B}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={%USERPROFILE}\Odyssey
DisableProgramGroupPage=yes
; The [Icons] "quicklaunchicon" entry uses {userappdata} but its [Tasks] entry has a proper IsAdminInstallMode Check.
UsedUserAreasWarning=no
LicenseFile=C:\odyssey\installer\eula.txt
;InfoBeforeFile=C:\odyssey\installer\preinstall.txt
;InfoAfterFile=C:\odyssey\installer\postinstall.txt
; Remove the following line to run in administrative install mode (install for all users.)
PrivilegesRequired=lowest
OutputBaseFilename=OdysseySetup
SetupIconFile=C:\odyssey\installer\touchofdeath.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked; OnlyBelowVersion: 6.1; Check: not IsAdminInstallMode

[Files]
Source: "C:\odyssey\installer\Odyssey.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\odyssey\installer\assets\*"; DestDir: "{app}\assets"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\odyssey\installer\jre\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\odyssey\installer\launcher.jar"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{userdesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[UninstallDelete]
Type: files; Name: "{app}\update.dat"
Type: files; Name: "{app}\game.jar"
Type: files; Name: "{app}\log.txt"
Type: files; Name: "{app}\whatsnew.txt"
Type: filesandordirs; Name: "{app}\assets"
Type: filesandordirs; Name: "{app}\maps"
Type: filesandordirs; Name: "{app}\jre"
Type: filesandordirs; Name: "{app}\logs"
Type: files; Name: "{app}\release.html"
Type: files; Name: "{app}\whatsnew.html" 
Type: dirifempty; Name: "{app}"