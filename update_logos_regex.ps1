$dir = "c:\Users\neo\Documents\HusayBaybay\app\src\main\res\layout"
$files = "activity_home.xml", "activity_games_menu.xml", "activity_dictionary_list.xml", "activity_word_detail.xml"

foreach ($fname in $files) {
    $fpath = Join-Path $dir $fname
    $c = Get-Content $fpath -Raw
    
    # Replace H
    $c = [regex]::Replace($c, '(?s)<TextView\s+android:layout_width="wrap_content"\s+android:layout_height="wrap_content"\s+android:text="H"(\s+)android:textColor="#67ACCD"\s+android:textSize="(\d+sp)"\s+android:textStyle="bold"\s*/>', '<TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="H"$1android:fontFamily="casual"$1android:textColor="#67ACCD" android:textSize="$2" android:textStyle="bold" />')
    
    # Replace B
    $c = [regex]::Replace($c, '(?s)<TextView\s+android:layout_width="wrap_content"\s+android:layout_height="wrap_content"\s+android:text="B"(\s+)android:layout_marginEnd="8dp"\s+android:textColor="#87B86A"\s+android:textSize="(\d+sp)"\s+android:textStyle="bold"\s*/>', '<TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="B"$1android:layout_marginEnd="8dp"$1android:fontFamily="casual"$1android:textColor="#87B86A" android:textSize="$2" android:textStyle="bold" />')
    
    # Replace Husay Baybay
    $c = [regex]::Replace($c, '(?s)<TextView(\s+)android:layout_width="wrap_content"(\s+)android:layout_height="wrap_content"(\s+)android:text="Husay Baybay"(\s+)android:layout_gravity="center_vertical"(\s+)android:textColor="#566573"(\s+)android:textSize="(\d+sp)"(\s+)android:textStyle="bold"\s*/>', '<TextView$1android:layout_width="wrap_content"$2android:layout_height="wrap_content"$3android:fontFamily="casual"$3android:text="Husay Bayba"$4android:layout_gravity="center_vertical"$5android:textColor="#566573"$6android:textSize="$7"$8android:textStyle="bold" />$1<TextView$1android:layout_width="wrap_content"$2android:layout_height="wrap_content"$3android:fontFamily="casual"$3android:text="y"$4android:layout_gravity="center_vertical"$5android:textColor="#67ACCD"$6android:textSize="$7"$8android:textStyle="bold" />')

    Set-Content -Path $fpath -Value $c -NoNewline
    Write-Output "Updated $fname"
}
