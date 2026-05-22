$dir = "c:\Users\neo\Documents\HusayBaybay\app\src\main\res\layout"
$files = Get-ChildItem -Path $dir -Filter "*.xml"

function Replace-Logo {
    param($content, $size1, $size2)
    
    $target = @"
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="H"
            android:textColor="#67ACCD"
            android:textSize="${size1}sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="B"
            android:layout_marginEnd="8dp"
            android:textColor="#87B86A"
            android:textSize="${size1}sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Husay Baybay"
            android:layout_gravity="center_vertical"
            android:textColor="#566573"
            android:textSize="${size2}sp"
            android:textStyle="bold" />
"@

    $replacement = @"
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:fontFamily="casual"
            android:text="H"
            android:textColor="#67ACCD"
            android:textSize="${size1}sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:fontFamily="casual"
            android:text="B"
            android:layout_marginEnd="8dp"
            android:textColor="#87B86A"
            android:textSize="${size1}sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:fontFamily="casual"
            android:text="Husay Bayba"
            android:layout_gravity="center_vertical"
            android:textColor="#566573"
            android:textSize="${size2}sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:fontFamily="casual"
            android:text="y"
            android:layout_gravity="center_vertical"
            android:textColor="#67ACCD"
            android:textSize="${size2}sp"
            android:textStyle="bold" />
"@

    $t = $target.Replace("`r`n", "`n")
    $r = $replacement.Replace("`r`n", "`n")
    return $content.Replace($t, $r)
}

foreach ($file in $files) {
    $c = Get-Content $file.FullName -Raw
    $oldC = $c
    $c = $c.Replace("`r`n", "`n")
    
    $c = Replace-Logo $c "56" "28"
    $c = Replace-Logo $c "48" "24"
    $c = Replace-Logo $c "40" "20"
    
    if ($oldC.Replace("`r`n", "`n") -ne $c) {
        $c = $c.Replace("`n", "`r`n")
        Set-Content -Path $file.FullName -Value $c -NoNewline
        Write-Output "Updated $($file.Name)"
    }
}
