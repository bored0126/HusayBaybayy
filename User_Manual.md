# HusayBaybay: Gabay at Manwal ng Paggamit (User Manual)

Maligayang pagdating sa **HusayBaybay**! Ito ay isang mobile learning application na idinisenyo upang pahusayin at subukin ang kasanayan sa baybay (spelling) at talasalitaan (vocabulary) ng wikang Filipino.

Ang manwal na ito ay nagbibigay ng detalyadong gabay para sa mga **Mag-aaral (Players)** at **Guro/Tagapangasiwa (Admins)**.

---

## 📌 Talaan ng Nilalaman

1. [Pagsisimula at Pag-login (Authentication)](#-pagsisimula-at-pag-login-authentication)
2. [Tahanan ng Mag-aaral (Player Home Screen)](#-tahanan-ng-mag-aaral-player-home-screen)
3. [Mga Mode ng Laro (Game Modes)](#-mga-mode-ng-laro-game-modes)
   - [Flash Pick Game](#1-flash-pick-game)
   - [Audio Game](#2-audio-game)
   - [Definition Game](#3-definition-game)
4. [Pamamahala sa Settings at Progress](#-pamamahala-sa-settings-at-progress)
5. [Dashboard ng Tagapangasiwa (Admin Dashboard)](#-dashboard-ng-tagapangasiwa-admin-dashboard)
6. [Gabay sa Pagsusuri ng Pagbabaybay (Spelling Rules Checklist)](#-gabay-sa-pagsusuri-ng-pagbabaybay-spelling-rules-checklist)

---

## 🔑 Pagsisimula at Pag-login (Authentication)

Gumagamit ang HusayBaybay ng ligtas na online database para sa pagsusuri ng progreso ng bawat manlalaro.

* **Pagpaparehistro (Registration):**
  Bago makapaglaro, kailangang magrehistro ng account. Ilagay ang iyong **Pangalan**, **Email**, **Password** (minimum na 6 na karakter), at piliin ang iyong **Seksyon (Section)** mula sa drop-down list upang maikonekta ang iyong score sa klase ng iyong guro.
* **Pag-login (Login):**
  Gamitin ang rehistradong email at password. Ang system ay may *automatic session retention*, kaya hindi mo na kailangang mag-login muli tuwing bubuksan ang app maliban kung ikaw ay mag-sign out.

> [!NOTE]
> Awtomatikong tinutukoy ng app kung ikaw ay isang **Player** (mag-aaral) o **Admin** (guro) base sa iyong account profile, at dadalhin ka sa tamang screen pagkatapos mag-login.

---

## 🏠 Tahanan ng Mag-aaral (Player Home Screen)

Pagkatapos mag-login bilang player, makikita ang limang pangunahing pindutan sa pangunahing screen:

| Pindutan                            | Paglalarawan / Description                                                                                          |
| :---------------------------------- | :------------------------------------------------------------------------------------------------------------------ |
| **Maglaro**                   | Bubuksan ang listahan ng tatlong nakakaaliw na spelling games.                                                      |
| **Talasalitaan (Dictionary)** | Diksiyonaryo ng mga salitang Filipino na may kasamang tamang kahulugan at spelling batay sa pambansang ortograpiya. |
| **Mga Aralin (Support Doc)**  | Built-in PDF reader na naglalaman ng mga materyales sa pag-aaral at gabay sa pagbabaybay.                           |
| **Settings**                  | Configuration panel para sa tunog, volume, at pag-reset ng laro.                                                    |
| **About**                     | Impormasyon tungkol sa layunin at gumawa ng application na HusayBaybay.                                             |

---

## 🎮 Mga Mode ng Laro (Game Modes)

May tatlong uri ng laro na may iba't ibang antas ng pagsubok sa pagbabaybay. Ang bawat laro ay may **30 katanungan** na awtomatikong nai-save ang progreso upang maipagpatuloy kung sakaling lumabas sa kalagitnaan.

---

### 1. Flash Pick Game

Isang mabilisang laro kung saan pipiliin ang tamang baybay ng salita mula sa tatlong (3) pagpipilian.

* **Mechanics:**
  * May limitasyong **30 segundo** bawat tanong.
  * Piliin ang pindutan na may tamang baybay (hal. `KORYENTE` vs `KURYENTE`).
  * Kung tama ang sagot, magpapakita ito ng kulay berdeng feedback; kung mali, magkukulay pula at mag-a-animate.
* **💡 Hint System:**
  * Awtomatikong lilitaw ang **Hint Button** (may icon na bumbilya) kapag umabot na sa **15 segundo o pababa** ang natitirang oras.
  * Pagkatapos itong i-tap, aalisin ang isang maling pagpipilian upang mas madaling mahanap ang tamang sagot.

---

### 2. Audio Game

Subukin ang iyong kakayahang makinig at magbaybay sa pamamagitan ng pagtype ng narinig na salita.

* **Mechanics:**
  * I-tap ang **Play Audio** upang pakinggan ang bigkas ng salita.
  * I-type ang tamang sagot sa text box at i-tap ang *Enter* o *Done* sa keyboard.
* **💡 Hint System (Play Counter Tracker):**
  * Kailangang pakinggan ang audio nang hindi bababa sa **tatlong (3) beses** upang ma-unlock ang Hint Button.
  * Ang pag-click sa Hint ay magpapakita ng:
    1. **Kahulugan (Meaning):** Depinisyon ng salita mula sa diksiyonaryo.
    2. **Pantig (Syllables):** Balangkas ng mga pantig na may nakabukad na unang titik (halimbawa, `[ B _ ] [ R A N ] [ G A Y ]` para sa *Barangay*).

---

### 3. Definition Game

Ayusin ang mga jumbled letters upang mabuo ang salitang inilalarawan sa depinisyon.

* **Mechanics:**
  * Basahin ang kahulugan ng salita sa screen.
  * Pindutin ang mga nagulong titik (jumbled letter tiles) sa ibaba upang punan ang mga bakanteng kahon sa itaas.
  * Kung nais baguhin, i-tap ang titik sa itaas na kahon upang ibalik ito sa ibaba.
  * Mayroong **Shuffle Button** upang baguhin ang pagkakaayos ng mga titik sa ibaba.
* **💡 Hint System & Nudges:**
  * Ang bilang ng pahiwatig na magagamit ay batay sa haba ng salita (kalahati ng bilang ng titik, minimum na 1).
  * Ang paggamit ng Hint ay awtomatikong maglalagay ng tamang titik sa susunod na bakanteng kahon at io-lock ito upang hindi na matanggal.
  * **Idle Nudge:** Kung hindi gumagalaw o nag-iisip ang manlalaro nang higit sa **12 segundo**, gagawa ng banayad na *shiver animation* ang Hint Button upang paalalahanan ang player na may tulong na magagamit.

---

## ⚙️ Pamamahala sa Settings at Progress

Sa tab ng **Settings**, maaaring ayusin ng mga manlalaro ang mga sumusunod:

* **Voice Volume Slider:** Lakas ng boses sa Audio Game.
* **Music Volume Slider:** Lakas ng background music ng app.
* **Tap Sound Switch:** I-on o i-off ang tunog kapag pumipili o pumipindot ng buttons.
* **Reset Progress Button:** Buburahin ang lahat ng kasalukuyang nakasagutan na antas sa tatlong laro at magsisimula muli sa tanong bilang 1. (Hindi nito binubura ang mataas na nakaraang iskor na naka-save sa database ng guro).

---

## 👩‍🏫 Dashboard ng Tagapangasiwa (Admin Dashboard)

Ang dashboard na ito ay eksklusibo para sa mga **Guro o Administrator** upang masubaybayan ang pag-unlad ng kanilang mga mag-aaral.

### Mga Tampok (Features):

1. **Pangkalahatang Istatistika:**
   * Makikita ang kabuuang bilang ng mga nakarehistrong mag-aaral.
   * Kabuuang bilang ng mga salita sa diksiyonaryo ng app.
2. **Pagsasala ayon sa Seksyon (Filter by Section):**
   * Maaaring gamitin ang dropdown menu upang tingnan lamang ang listahan ng mga mag-aaral sa isang partikular na seksyon (hal. *Grade 4 - Narra*).
3. **Pagsusuri ng Iskor at Progreso:**
   * Ipinapakita ng table/listahan ang pangalan at email ng bawat mag-aaral.
   * **Flash Pick Progress:** Huling Iskor at Pinakamataas na Iskor (High Score).
   * **Audio Game Progress:** Bilang ng mga natapos na salita (Words Completed).
   * **Definition Game Progress:** Bilang ng mga natapos na salita (Words Completed).
4. **Pamamahala ng Seksyon (Manage Sections):**
   * Pindutin ang *Manage Sections* button upang magdagdag ng mga bagong pangalan ng klase/seksyon na maaaring piliin ng mga bagong mag-aaral habang nagpaparehistro.
   * Maaari ring magbura ng mga lumang seksyon na hindi na ginagamit.

---

## 📖 Gabay sa Pagsusuri ng Pagbabaybay (Spelling Rules Checklist)

Ang diksiyonaryo ng HusayBaybay ay sumusunod sa pambansang pamantayan ng ortograpiya ng wikang Filipino. Narito ang ilang halimbawa ng tamang spelling na matututuhan sa app:

* **Paggamit ng Gitling (Hyphenation):**
  * `Ano-ano` (Tama) ❌ `Anu-ano` (Mali)
  * `Iba't iba` (Tama) ❌ `Iba-iba` (Mali)
* **Pagbabago ng Katinig batay sa Kahulugan:**
  * `Haluhalo` (Pagkaing may yelo at gatas) vs `Halo-halo` (Pinagsama-samang bagay na walang kaayusan)
  * `Salusalo` (Isang piging/handaan) vs `Salo-salo` (Magkakasamang kumakain)
* **Wastong Hiram na Salita:**
  * `Koryente` (Mula sa Kastilang *corriente*) ❌ `Kuryente` o `Kuryenti` (Mali)
  * `Ebidensiya` (Tama) ❌ `Ebidensya` (Mali)
  * `Estudyante` (Tama) ❌ `Istudyante` (Mali)

---
