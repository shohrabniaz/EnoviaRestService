@ECHO off
SETLOCAL

REM Please varify the following two path
SET mcs_dir=H:\DassaultSystemes\R2021x\3DSpace
SET backup_file_location=H:\DassaultSystemes\Widget_Backup

REM Dont change the following variable 'deployment_environment'
SET deployment_environment=21x_PREPROD

SET widget_title=PLM_PDM_BOM_Comparison
SET widget_folder_name=PLMPDMBOMCompareUX

SET hour=%time: =0%
SET datetime=%date:~10,4%-%date:~4,2%-%date:~7,2%_%hour:~0,2%-%time:~3,2%-%time:~6,2%
SET log_path=%~dp0\%widget_title%_Deployment_log_%datetime%.txt

SET script_path=%~dp0

SET number_of_instances=10
SET app_root_dir_path=%mcs_dir%\STAGING\ematrix\
SET instance_dir_path=%mcs_dir%\win_b64\code\

IF EXIST %app_root_dir_path%webapps\%widget_folder_name% (
	REM File Backup from STAGING
	Robocopy /MIR %app_root_dir_path%webapps\%widget_folder_name% %backup_file_location%\%widget_title%_backup\%datetime%\%widget_folder_name% /LOG+:%log_path%
	
	ECHO Previous Deployment Backup Directory: %backup_file_location%\%widget_title%_backup\%datetime%\
)

FOR %%I IN ("%script_path%") DO SET "source_path=%%~fI"
Robocopy /MIR %source_path%\%widget_folder_name% %app_root_dir_path%webapps\%widget_folder_name% /LOG+:%log_path%
Robocopy %source_path%\WidgetConfiguration\%deployment_environment% %app_root_dir_path%webapps\%widget_folder_name%\assets\js\Configuration WidgetConfiguration.js /LOG+:%log_path%

IF EXIST %instance_dir_path%tomee (
    Robocopy /MIR %source_path%\%widget_folder_name% %instance_dir_path%tomee\webapps\3dspace\webapps\%widget_folder_name% /LOG+:%log_path%
	Robocopy %source_path%\WidgetConfiguration\%deployment_environment% %instance_dir_path%tomee\webapps\3dspace\webapps\%widget_folder_name%\assets\js\Configuration WidgetConfiguration.js /LOG+:%log_path%
)

FOR /L %%A IN (1,1,%number_of_instances%) DO (
    IF EXIST %instance_dir_path%tomee%%A (
        Robocopy /MIR %source_path%\%widget_folder_name% %instance_dir_path%tomee%%A\webapps\3dspace\webapps\%widget_folder_name% /LOG+:%log_path%
		Robocopy %source_path%\WidgetConfiguration\%deployment_environment% %instance_dir_path%tomee%%A\webapps\3dspace\webapps\%widget_folder_name%\assets\js\Configuration WidgetConfiguration.js /LOG+:%log_path%
    )
)

@ECHO:
@ECHO:
ECHO  SUCCESS: Deployment Done,Check Log For Details...
@ECHO:
@ECHO:
PAUSE