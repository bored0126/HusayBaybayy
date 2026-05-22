# PROJECT PROPOSAL: HUSAYBAYBAY

### **TITLE:** 
HusayBaybay: A Gamified Mobile Learning Application for Filipino Orthography and Spelling Proficiency

### **DESCRIPTION:**
**HusayBaybay** is an offline-capable, interactive mobile learning application aimed at developing elementary students' skills in the proper spelling and orthography of Filipino words. The application’s design and vocabulary standards are strictly aligned with the pambansang ortograpiya of the **Komisyon sa Wikang Filipino (KWF)**. 

To bridge the gap between traditional learning and modern technology, HusayBaybay features a gamified ecosystem consisting of a interactive Spelling Exercises Suite, a rich Mini-Dictionary, study guides, and a real-time Teacher/Admin Dashboard. This application is developed for a research group consisting of third-year Filipino Major students of **Mabini Colleges**, serving as a tool to measure and enhance students' spelling proficiency in classroom settings.

---

### **TARGET USERS/CLIENTS:**
1.  **The Research Group Members:** To monitor, filter, and extract spelling scores and progress logs for research data collection.
2.  **Elementary Students (Players):** To learn Filipino spelling rules in a highly engaging, interactive, and gamified environment.
3.  **Teachers/Administrators:** To manage class sections and evaluate students' performance metrics in real-time.

---

System Architecture & Key Features
==================================

### **1. USER REGISTRATION & ROLE-BASED ACCESS CONTROL (NEW)**
*   **Student Registration:** Students register by inputting their Name, Email, Password, and selecting their specific Class Section from a dynamic dropdown list.
*   **Role-Based Routing:** The app uses Firebase Firestore to identify whether a logging-in account is a **Player** or an **Admin (Teacher)**, automatically redirecting them to their respective dashboards.
*   **Session Retention:** Active logins are securely cached so users do not have to re-authenticate every time they launch the app.

### **2. THE INTERACTIVE GAMES SUITE (SPELLING EXERCISES)**
Consists of three structured spelling games containing 30 specialized levels each. The system features a **Game State Recovery System (NEW)** using `SharedPreferences`, enabling students to pause and resume their exact question progress even if the app is closed.

*   **Game 1: Flash Pick Game (Multiple Choice)**
    *   *Mechanics:* Users choose the correct spelling of a word from three options.
    *   *Gamified Constraints:* Includes a strict **30-second countdown timer** per question.
    *   *Adaptive Hint System (NEW):* A lightbulb hint button appears when the timer drops below 15 seconds, allowing students to eliminate one incorrect option.
    *   *Kinetic Feedback (NEW):* Elements pulse on correct answers and shake on incorrect answers.
*   **Game 2: Audio Game (Voice to Text spelling)**
    *   *Mechanics:* Users tap a button to hear the audio pronunciation of a KWF-standard word and type the exact spelling.
    *   *Play-Counter Tracker (NEW):* Encourages active listening by requiring the student to listen to the pronunciation at least **three (3) times** before unlocking the Hint button.
    *   *Syllable-Based Hints (NEW):* Shows a visual breakdown of syllables with blank letter placeholders (e.g., `[ B _ ] [ R A N ] [ G A Y ]` for *Barangay*) alongside its definition.
*   **Game 3: Definition Game (Jumbled Letters)**
    *   *Mechanics:* Displays a KWF dictionary definition, and students click/tap jumbled letter tiles to arrange them into the correct word.
    *   *Intelligent Hint System (NEW):* Automatically locks correct letters into the next available slot based on word length.
    *   *Shuffle Tool (NEW):* Allows students to randomize the letter layouts to gain a fresh perspective.
    *   *Idle Nudge/Shiver Animation (NEW):* If a student is idle for more than 12 seconds, the Hint button gently shivers to prompt the student that help is available.

### **3. INTEGRATED MINI-DICTIONARY & STUDY MATERIALS**
*   **KWF-Standard Mini-Dictionary:** An alphabetical (A–Z) index of Filipino words. Tapping any word opens a clean detail screen showing its official spelling and KWF-prescribed definition.
*   **Offline Study Materials (NEW):** Integrated native PDF reader powered by `PdfRenderer` that allows students to study the comprehensive guide `Studies - HusayBaybay.pdf` directly inside the app with high performance.

### **4. REAL-TIME TEACHER / ADMIN DASHBOARD (NEW)**
*   **Statistical Counters:** Displays real-time aggregations of total registered students and dictionary vocabulary size.
*   **Dynamic Class Filters:** Teachers can filter student scoreboards dynamically by choosing a specific section.
*   **Comprehensive Progress Tracker:** Displays a complete report card for each student, tracking:
    *   *Flash Pick:* Last Score and High Score.
    *   *Audio Game:* Completed Words Count (out of 30).
    *   *Definition Game:* Completed Words Count (out of 30).
*   **Section Manager:** Allows teachers to add or delete school sections dynamically in Firestore, which immediately updates the student registration dropdown.

### **5. AUDIOVISUALS & SYSTEM SETTINGS**
*   **Volume Controls (NEW):** Separate configuration seek-bars for Text-to-Speech (TTS) voice volume and looping background music volume.
*   **Tap Sound Effects:** Option to toggle button-click auditory feedback.
*   **Secure Reset Progress:** Allows players to reset their current game progression while preserving high scores in the teacher's database.

---

Developer & Researcher Roles
============================
*   **Researchers (Mabini Colleges Students):** To provide the specific vocabulary lists, definitions, and high-quality voice pronunciation assets for the Audio Game.
*   **Developer (Suggested Soundscapes):** Implemented a professional, loopable, warm background music track manager and custom click sounds.
*   **Database Hosting:** Supported by Google Firebase Authentication and Cloud Firestore for real-time, offline-capable database operations.
