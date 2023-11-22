# BG Multiviewer 
### 운전 중 혈당 Viewer
##### 혈당 모니터링 어플 BG_Multiviewer를 소개합니다

카이스트 전산학부 CS492 전산학 특강 <Tech for Impact>에서 진행된 프로젝트로, 운전 중에도 수시로 혈당을 모니터링하기 위해서 네비게이션과 함께 이용가능한 듀얼 뷰어 어플을 제작하게 되었습니다.
팝업, 듀얼스크린 모드를 이용하면서 운전 중 뿐만 아니라 다른 어플을 방해받지 않고 이용해보세요!

제 1형 당뇨병(췌도부전) 환자들을 위해 실시간 혈당을 표시하는 안드로이드 앱입니다. Nightscout 주소를 입력하면 혈당 데이터를 받아와 화면 비율에 맞춰 혈당 값이랑 혈당 기록을 점그래프로 나타내어 출력해주는 역할을 합니다.
화면 분할, 팝업시에도 비율에 따라 적절하게 정보를 표시해줍니다. 위험 혈당 범위를 설정할 수 있고, 위험 혈당 범위를 넘어가면 표시되는 데이터의 색이 바뀌고 진동과 알림음이 울립니다. 데이터는 5분마다 갱신됩니다.
현재 Nightscout에서 받아오는 데이터만 지원하고 있습니다.

## 사용 방법
..
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

## 화면

## LICENSE
라이센스 명시

## 연락처
문제 발생시 연락할 연락처, 메일 주소, SNS 주소 입력 필요




This is an Android app designed for type 1 diabetes patients (with pancreatic failure), which displays real-time blood sugar levels. By entering the Nightscout address, it fetches blood sugar data and displays blood sugar values and records as a dot graph, proportionate to the screen ratio.
It appropriately displays information according to the screen split and pop-up ratio. You can set a dangerous blood sugar range, and if the blood sugar level exceeds this range, the color of the displayed data changes and vibrations and alerts sound. Data is updated every 5 minutes.
Currently, it only supports data fetched from Nightscout.