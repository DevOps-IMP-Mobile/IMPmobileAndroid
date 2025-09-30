# IMPmobileAndroid
## DevOps Project Management Android App
> Clean Architecture + MVI Pattern 기반 프로젝트 관리 Android 앱 <br>
25.01.17 - 25.05.30

[![Project Management Cover](https://github.com/user-attachments/assets/925576e1-9379-4e04-b277-293910569ab0)](https://eggplant-piccolo-90a.notion.site/21a5c454f14581aa8ee4c42f6c358d6c?pvs=74)
---

## 🎥 시연 영상
<p align="center">
  <a href="https://www.youtube.com/watch?v=nuIPEyN1rqU">
    <img src="https://img.youtube.com/vi/nuIPEyN1rqU/0.jpg" alt="시연 영상" width="600"/>
  </a>
</p>

---
## 📱 Contributors
| 성규현 (Android Lead) <br> [@dmp100](https://github.com/dmp100) | 이승비 (Android Developer) <br> [@lime13579](https://github.com/lime13579) | 양정우 (PM) <br> [@mrangjw](https://github.com/mrangjw) |
|:---:|:---:|:---:|
| <img width="150" src="https://avatars.githubusercontent.com/u/107687577?v=4"/> | <img width="150" src="https://github.com/user-attachments/assets/1afd184d-2e42-42d5-9698-8aaaefb64a4e"/> | <img width="150" src="https://avatars.githubusercontent.com/u/157506327?v=4"/> |
| 프로젝트 관리 Android 앱 개발<br>UI/UX 설계, Clean Architecture 구현<br>프로젝트 관리, 태스크 트래킹, 대시보드 시스템 | 프로필 화면 제작 및 API 연결<br>사용자 프로필 UI/UX 구현<br>프로필 API 연동 및 데이터 관리 | PM 및 API 연결<br>프로젝트 기획 및 관리<br>백엔드 API 포팅 및 연동 |


<br/>


## 🟨 SCREENSHOT

| 로그인 | 홈 대시보드 | 프로젝트 |
|:---:|:---:|:---:|
| <img width="200" src="https://github.com/user-attachments/assets/c1e62bdc-0b64-412b-9cda-74d452763ab1"/> | <img width="200" src="https://github.com/user-attachments/assets/68dd8125-a781-4ec1-a682-d4aeddd4bad1"/> | <img width="200" src="https://github.com/user-attachments/assets/e6fb192b-d744-4cce-bec5-5e33943ee62d"/> |
| 이슈 관리 | 이슈 생성,수정 | 프로필 |
| <img width="200" src="https://github.com/user-attachments/assets/b4e6fa91-545a-4ce1-a366-43c586ae5ee0"/> | <img width="200" src="https://github.com/user-attachments/assets/53e4ed63-758e-44d4-9285-9c1537182f5a"/> | <img width="200" src="https://github.com/user-attachments/assets/d68658af-9a65-410f-be89-f70126cf27f4"/> |

<br/>

## 💡 프로젝트 배경
Project Management Android App은 기존 PORTAL/ITS/QMS 통합 웹 시스템에서 핵심 기능만을 추출하여 모바일 환경에 최적화한 프로젝트 관리 도구입니다.

### 해결하고자 하는 문제
- 🖥️ PC 의존적 업무: 기존 웹 시스템의 모바일 접근성 한계 해결

- 📱 언제 어디서나: 외부에서도 실시간 프로젝트 현황 확인 및 관리

- ⚡ 빠른 대응: 이슈 발생 시 즉시 확인하고 대응할 수 있는 모바일 환경 제공
- 🎯 핵심 기능 집중: 웹 시스템의 복잡한 기능 중 모바일에서 꼭 필요한 기능만 선별


## 🌐 웹 시스템 연계 기능 (Web Integration Features)
Project Management Android App은 기존의 **PORTAL/ITS/QMS 통합 웹 시스템**과 연동되며, 모바일 환경에서 다음과 같은 핵심 기능을 제공합니다. 이 기능들은 웹에서 제공하던 복잡한 기능 중 실제 모바일 사용자에게 **가장 필요한 기능들만 선별**하여, **간편하고 빠른 UI/UX**로 재구성되었습니다.

📸 **아래에 첨부된 모든 UI 이미지는 기존 웹 시스템에서 직접 캡처한 실제 화면입니다.**  
모바일 앱은 이 웹 시스템의 핵심 기능만 발췌하여 최적화된 형태로 재구성되었습니다.

---

### 🏠 대시보드 (Dashboard)
- **웹 시스템 연계 내용**:  
  - 웹에서는 전체 프로젝트, 이슈, 완료율 등의 다양한 통계를 제공  
  - 부서별, 기간별 필터 및 그래프 기반 대시보드 제공
- **모바일 최적화 방식**:  
  - 주요 프로젝트 수, 진행률, 이슈 현황 등 요약된 핵심 지표만 시각적으로 표시  
  - 불필요한 세부 통계는 제외하고, 사용자가 즉시 인지 가능한 요소만 구성  
  - **Jetpack Compose 기반 카드 UI**로 빠르게 핵심 정보 확인 가능  

📷 *웹 시스템 대시보드 화면 캡처*  
<img width="1916" height="998" alt="Dashboard Screenshot" src="https://github.com/user-attachments/assets/7b182b22-bf29-483d-9f96-eebef4d6357a" />

---

### 📋 프로젝트 관리 (Project Management)
- **웹 시스템 연계 내용**:  
  - 프로젝트 생성, 팀원 관리, 역할별 권한 설정, 일정관리 기능 제공  
  - 대규모 프로젝트/팀 기준으로 복잡한 정보 구성
- **모바일 최적화 방식**:  
  - 사용자 본인이 속한 프로젝트만 필터링해 표시  
  - 팀원 목록은 최소 정보 (이름, 직책, 연락처)만 노출  
  - 프로젝트 진척률/상태를 직관적인 색상 및 태그로 구분  

📷 *웹 시스템 프로젝트 목록 화면 캡처*  
<img width="1920" height="1059" alt="Project List Screenshot" src="https://github.com/user-attachments/assets/cfc6a347-846b-4c2f-847b-745c8c65edef" />

---

### 🔧 이슈 트래킹 (Issue Tracking)
- **웹 시스템 연계 내용**:  
  - ITS(이슈 트래킹 시스템)를 통해 상세한 이슈 등록, 상태 변경, 담당자 지정 기능 제공  
  - 첨부파일, 댓글, 로그 히스토리 등 다양한 서브 기능 포함
- **모바일 최적화 방식**:  
  - 이슈 조회/수정/삭제/생성 가능  
  - 간단한 필터(상태, 중요도) 기능 추가  
  - UI는 리스트 기반으로 빠른 확인 가능하도록 구현  
  - **이슈 상세 정보 확인** 및 **상태 변경**은 최소한의 터치로 완료 가능  

📷 *웹 시스템 이슈 목록 화면 캡처*  
<img width="1920" height="1076" alt="Issue List" src="https://github.com/user-attachments/assets/8efcae1a-eabd-4742-9fba-ed5c6523079a" />

📷 *웹 시스템 이슈 삭제 화면 캡처*  
<img width="1918" height="1064" alt="Issue Delete" src="https://github.com/user-attachments/assets/bb26e097-696e-497f-aef0-c3f6f55b24f8" />

📷 *웹 시스템 이슈 수정 및 추가 화면 캡처*  
<img width="1920" height="1080" alt="Issue Edit" src="https://github.com/user-attachments/assets/72d01418-94ce-4ee9-a6fd-e0ccf8cde1ce" />

---

### 👤 사용자 관리 (User Management)
- **웹 시스템 연계 내용**:  
  - 사내 계정 연동, 권한 부여, 역할별 접근 제어 등 복잡한 사용자 인증 시스템 구성  
  - 관리자 페이지에서 사용자 관리, 권한 조회 가능
- **모바일 최적화 방식**:  
  - 이메일/비밀번호 기반 로그인 처리  
  - 로그인 후 사용자 정보를 앱 내부 프로필에 반영  
  - 사용자 권한에 따른 UI 요소 차별화 (예: 관리자 전용 기능 숨김/노출)  
  - 프로필 화면에서 개인 정보 확인 및 간단한 정보 수정 가능  

📷 *웹 시스템 사용자 정보 화면 캡처*  
<img width="1920" height="1080" alt="User Profile Screenshot" src="https://github.com/user-attachments/assets/dc1899a3-2b54-42b8-990a-69ae0a8405dd" />

---

이와 같이, 기존 **대규모 웹 시스템과의 기능 연계를 유지하면서**, **모바일 UX에 최적화된 구조**로 기능을 간소화하고 설계하였습니다. 주요 목적은 **정보 전달 속도와 효율성**, 그리고 **사용자 중심의 인터페이스 구현**입니다.

<br/>

## 🔧 TECH STACKS
| Category | TechStack |
| --- | --- |
| Architecture | Clean Architecture, MVI Pattern, 멀티모듈 |
| Language | Kotlin |
| UI | Jetpack Compose, Material Design 3 |
| Dependency Injection | Hilt |
| Network | Retrofit, OkHttp, Kotlinx Serialization |
| Asynchronous | Coroutines, Flow |
| State Management | StateFlow, SharedFlow |
| Navigation | Navigation Compose |
| Local Database | Room |
| Analytics | Firebase |

<br/>

## 📁 Foldering
```
📂 com.example.myapplication
┣ 📂 core
┃ ┣ 📂 ui
┃ ┃ ┣ 📂 theme
┃ ┃ ┗ 📂 component
┃ ┣ 📂 data
┃ ┣ 📂 network
┃ ┣ 📂 database
┃ ┗ 📂 domain
┃   ┣ 📂 model
┃   ┣ 📂 repository
┃   ┗ 📂 usecase
┗ 📂 feature
  ┣ 📂 login
  ┣ 📂 home
  ┣ 📂 project
  ┣ 📂 issue
  ┗ 📂 profile
```

---
> #### © 2025 Project Management App | Smart Task Tracker for Teams | All Rights Reserved
