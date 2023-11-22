# BG Multiviewer 
### 운전 중 혈당 Viewer
##### 혈당 모니터링 어플 BG_Multiviewer를 소개합니다

카이스트 전산학부 CS492 전산학 특강 &lt;Tech for Impact&gt;에서 진행된 프로젝트로, 운전 중에도 수시로 혈당을 모니터링하기 위해서 네비게이션과 함께 이용가능한 듀얼 뷰어 어플을 제작하게 되었습니다.
팝업, 듀얼스크린 모드를 이용하면서 운전 중 뿐만 아니라 다른 어플을 방해받지 않고 이용해보세요!

제 1형 당뇨병(췌도부전) 환자들을 위해 실시간 혈당을 표시하는 안드로이드 앱입니다. Nightscout 주소를 입력하면 혈당 데이터를 받아와 화면 비율에 맞춰 혈당 값이랑 혈당 기록을 점그래프로 나타내어 출력해주는 역할을 합니다.
화면 분할, 팝업시에도 비율에 따라 적절하게 정보를 표시해줍니다. 위험 혈당 범위를 설정할 수 있고, 위험 혈당 범위를 넘어가면 표시되는 데이터의 색이 바뀌고 진동과 알림음이 울립니다. 데이터는 5분마다 갱신됩니다.
현재 Nightscout에서 받아오는 데이터만 지원하고 있습니다.

## 사용 방법
apk 사용방법
초기설정
1. BG Multiviewer.apk 파일을 설치 후 실행시켜 줍니다
2. 초기에 본인의 cgm 정보가 올라오는 서버의 url을 입력해주시면 됩니다.

메인 기능
- 메인 화면은 혈당, 혈당 변화 그래프, 혈당 화살표 및 변화값, 현재 시간, 최근 업데이트 시간, IOB, COB, BASOL가 표시됩니다
- IOB, COB, BASOL은 표시 여부를 수정할 수 있습니다
- 메인 화면을 터치하면 설정에 진입 할 수 있는 상단 바가 노출됩니다.
- 긴급 혈당 고혈당, 저혈당 범위에 진입시 혈당을 나타내는 숫자와 배경색이 변경되며 알림과 진동으로 사용자에게 신호를 전달합니다

환경설정
- 설정 화면에서는 고혈당, 저혈당 및 긴급 혈당의 범위를 사용자가 직접 설정할 수 있습니다. 기타 색상 및 폰트 크기도 변경이 가능합니다
- 저혈당, 고혈당 진입시 울리는 진동과 알림도 on, off 가능합니다
- 입력된 url을 변경하고 싶은 경우 nightscout 주소 재설정 버튼을 터치하시면 됩니다
### 주의사항
혈당을 받아오는 사이트 중 nightscoutpro와 railway만 적용 가능합니다.
인터넷 연결상태를 상시 확인해주세요.
### 파일 설명
BG.kt: 혈당 데이터의 형식을 저장합니다.

BGData.kt: 혈당 데이터를 처리하여 저장합니다.

CommonActivity.kt: MultiViewActivity와 SingleViewActivity 공통 요소를 구현합니다.

Data_Courutine.kt: 비동기 방식으로 URL에서 혈당 데이터를 가져옵니다.

GraphThread.kt: 그래프 출력 프로그램을 관리합니다.

MultiViewActivity.kt: 분할 화면에서 혈당 정보를 표시합니다.

PreferencesActivity.kt: 여러 변수들 (위험 혈당 범위, 알림음 설정 등)을 설정하는 설정창을 표시합니다.

SingleViewActivity.kt: 기본 화면에서 혈당 정보를 표시합니다.

StartURLActivity.kt: 시작 화면을 출력하며, URL을 입력받습니다.

UtilFunction.kt: 여러 편의성 함수들을 정의합니다.

## 제작
### 팀원
김동주(KAIST, SoC) - 팀장

김재호(KAIST, SoC)

김혜연(KAIST, SoC)

박찬호(KAIST, SoC)

정지광(KAIST, ISysE)

차연우(KAIST, SoC)

### 팀 소개
CS492 전산학 특강 &lt;Tech for Impact&gt;의 '운전 중 혈당 Viewer'팀은 혈당 모니터링 어플리케이션, BG Multiviewer를 개발하였습니다. 1형 당뇨병 환우회의 김미영 대표님과 카카오 멘토님의 피드백을 바탕으로 진행되었으며, 사용자들의 필요와 경험을 직접 반영하였습니다.


## LICENSE
MIT 라이센스를 기본으로 합니다. 누구나 사용, 수정 및 재배포, 2차적인 저작물 개발도 가능합니다.

다만, 프로젝트 취지 상 상업적 이용 자제를 권장합니다.

## 연락처
문제 발생 및 추가사항 문의는 skipper080837@gmail.com로 메일 남겨주시기 바랍니다.



## Driving Blood Glucose Viewer
### Introducing the BG Multiviewer App for Blood Glucose Monitoring

This project, developed under KAIST's Computer Science course CS492 <Tech for Impact>, focuses on creating a dual-viewer app that allows continuous blood glucose monitoring while driving. The app can be used in conjunction with navigation apps, utilizing popup and dual-screen modes for convenience.

BG Multiviewer is a real-time blood glucose display app for Android, specifically designed for Type 1 diabetes (pancreatic failure) patients. By entering your Nightscout URL, it fetches blood glucose data and displays it in a responsive graph, adapting to different screen ratios during split-screen or popup mode. The app alerts users with color changes, vibrations, and sound notifications when blood glucose levels enter predefined danger zones. Data refreshes every five minutes. Currently, it only supports data from Nightscout.

## How to Use
apk Instructions
Initial Setup
1. Install and run the BG Multiviewer.apk file.
2. Enter the URL of the server where your CGM data is uploaded.

Main Features
- The main screen displays blood glucose levels, change graphs, directional arrows and change values, current time, last update time, IOB, COB, and BASOL.
- You can toggle the display of IOB, COB, BASOL.
- Touching the main screen reveals a top bar for accessing settings.
- Entering high or low blood glucose emergency ranges changes the display color of the data and triggers alerts and vibrations.

Settings(Preferences)
- Customize high and low blood glucose and emergency ranges. Font size and colors are also adjustable.
- Vibration and alert notifications for high and low blood glucose can be turned on or off.
- To change the entered URL, touch the reset Nightscout address button.

## Notes
The app currently supports Nightscoutpro and railway sites only.
Please ensure a stable internet connection.

## File Descriptions
BG.kt: Stores the format of blood glucose data.

BGData.kt: Processes and stores blood glucose data.

CommonActivity.kt: Implements common elements of MultiViewActivity and SingleViewActivity.

Data_Courutine.kt: Fetches blood glucose data from URL asynchronously.

GraphThread.kt: Manages the graph output program.

MultiViewActivity.kt: Displays blood glucose information in split-screen mode.

PreferencesActivity.kt: Displays settings for various variables (dangerous blood glucose ranges, alert settings, etc.).

SingleViewActivity.kt: Displays blood glucose information on the standard screen.

StartURLActivity.kt: Displays the start screen and receives URL input.

UtilFunction.kt: Defines various utility functions.

## Development
### Team Members
Kim Dong-Joo (KAIST, SoC) - Team Leader

Kim Jae-Ho (KAIST, SoC)

Kim Hye-Yeon (KAIST, SoC)

Park Chan-Ho (KAIST, SoC)

Jung Ji-Kwang (KAIST, ISysE)

Cha Yeon-Woo (KAIST, SoC)

### Team Introduction
The 'Driving Blood Glucose Viewer' team from CS492 <Tech for Impact> at KAIST developed the BG Multiviewer app. The development was guided by feedback from Kim Mi-Young, representative of the Type 1 Diabetes Association, and a Kakao mentor, ensuring the app meets the needs and experiences of its users.

## LICENSE
This project is under the MIT License, allowing free use, modification, redistribution, and development of derivative works.

However, in line with the project's ethos, commercial use is discouraged.

## Contact
For issues or further inquiries, please email skipper080837@gmail.com.
