$dir = "c:\Users\neo\Documents\HusayBaybay\app\src\main\res\layout"
$files = Get-ChildItem -Path $dir -Filter "*activity_*.xml"

foreach ($file in $files) {
    if ($file.Name -eq "activity_main.xml") { continue }
    
    $c = Get-Content $file.FullName -Raw
    
    # We replace the root background using regex on the first match
    $c = [regex]::Replace($c, 'android:background="#[Ff]{6}"(?s)([^<]*?)>', 'android:background="@drawable/bg_app_cloudy"$1>', 1)
    
    Set-Content -Path $file.FullName -Value $c -NoNewline
    Write-Output "Updated $($file.Name)"
}
