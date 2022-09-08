Get-Date | Out-File -Append G:\Integrations\LNTransfer\Cost_Data_Update_Scheduler.log
Invoke-WebRequest -Uri 'https://dsd3v21integration.plm.valmet.com/EnoviaRestService/valmet/enovia/api/nightly/items/cost-data' | Out-File -Append G:\Integrations\LNTransfer\Cost_Data_Update_Scheduler.log
Get-Date | Out-File -Append G:\Integrations\LNTransfer\Cost_Data_Update_Scheduler.log