@echo off

set SITSourcePath=\\V0648A\Integrations\MT
set SITDestinationPath=G:\Integrations\
set EIA_pipe_in_dir=G:\Integrations\MT\FromPDM
set EIA_pipe_out_dir=G:\Integrations\MT\ToPDM
set VAL_Import_dir=G:\PDM_Enovia_VAL_Integration\VAL_IMPORT_LIST
set VAL_Import__old_dir=G:\PDM_Enovia_VAL_Integration\VAL_IMPORT_LIST\old



rem Create directory hierarchy
md %SITDestinationPath%
md %EIA_pipe_in_dir%\Error
md %EIA_pipe_in_dir%\history
md %EIA_pipe_in_dir%\old
md %VAL_Import__old_dir%

rem Share EIA corresponding Folder the server where web service is running 
mklink /D %SITDestinationPath%\MT %SITSourcePath%

rem everyone r/w access
icacls %EIA_pipe_in_dir% /t  /grant Everyone:F
icacls %EIA_pipe_out_dir% /t  /grant Everyone:F
icacls %VAL_Import_dir% /t  /grant Everyone:F
icacls %VAL_Import__old_dir% /t  /grant Everyone:F
pause

