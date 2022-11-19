## CODEMOIM (https://www.codemoim.com)

> ### 목차
1. 프로젝트 설명
2. 사용기술
3. 기능정의
4. 프로젝트 구조

---

### 1. 프로젝트 설명
#### [개발자를 위한 지식 공유 사이트]
- 개발에 대한 질문과 답변을 코드와 이미지를 포함해서 작성할 수 있습니다.
- 게시글에 대한 추천, 비추천, 스크랩을 할 수 있습니다.
- 게시글, 댓글에 관한 알림을 받을 수 있습니다.
- 태그별로 게시글을 볼 수 있고 태그와 관련된 게시글을 검색할 수 있습니다.
- 사이트 기여도에 따라 회원별 기여도 점수가 올라가게 되고 순위를 확인할 수 있습니다.
- 게시글/팔로워를 기준으로 한 태그 순위를 확인할 수 있습니다.

<br/>

---

<br/>

### 2. 사용기술
#### [프론트 엔드]
- React
- Redux Toolkit
- React Router
- Webpack
- Ant Design
#### [백엔드]
- Spring Boot
- Spring Security
- Oauth2
- JWT
- JPA & QueryDSL
- Hibernate Search6 + nori 한글 형태소 분석기
- RabbitMQ + HAProxy + Keepalived
- Junit
#### [데이터베이스]
- AWS RDS(MySQL)
#### [배포+인프라]
- AWS EC2
- AWS S3
- AWS Cloudfront
- AWS Parameter Store
- AWS ELB(Application Load Balancer)
- AWS Auto Scaling
- AWS CodeDeploy
- AWS CloudWatch
- Jenkins

<br/>

---

<br/>

### 3. 기능정의

**1. 로그인**
> 깃허브, 카카오, 네이버를 통한 로그인이 가능합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/142091425-21ccaed3-e3ef-40c5-9b5e-3242a88e4423.png" width="80%" height="80%"></p>

<br/>

**2. 비밀번호 찾기**
> 회원가입 시 입력한 이메일 주소를 통해 변경 메일을 전송받고 메일을 통해서 비밀번호를 찾을 수 있습니다.

1. 회원가입 시 입력한 이메일 주소를 입력합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/140902121-3461fc07-b5cc-41ab-a9b8-c877c6124da1.png" width="80%" height="80%"></p>

2. 입력한 이메일로 비밀번호 찾기 메일이 전송됩니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/136660248-7c485a8d-cb3b-4b13-833b-0b1fdf1fbdff.png" width="80%" height="80%"></p>

3. 메일을 열람 후 비밀번호 변경 버튼을 클릭합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/140902611-2309d515-72e9-42aa-a7f9-f832a537ad88.png" width="55%" height="55%"></p>

4. 비밀번호 변경 페이지로 이동이 되고 해당 페이지에서 변경할 비밀번호를 입력 후 수정합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/140902788-6f4564d9-0a42-4251-bf37-b86f0160a799.png" width="80%" height="80%"></p>

<br/>

**3. 회원가입**
> Username, Password, Email 모두 필수값으로 설정되어 있으며, 공란인 경우 경고를 출력하고 Username, Email은 중복체크를 실행합니다.

<p align="center"><img src="https://user-images.githubusercontent.com/73155105/140901720-930785ec-96b9-4926-a540-2b53b73b859d.png"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/140901620-0e847db6-1ad3-47ea-a375-b80c8604dc20.png"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141705367-45096b51-5907-4a3f-99c9-4f3aa7b4f95d.png"></p>

<div style="page-break-after: always;"></div>

**4. 메인페이지**
> 게시글의 조회수를 기준으로 상위 5개의 인기글을 표시, 추천수를 기준으로 상위 5개의 추천글을 표시합니다. Q & A, 자유게시판의 경우 작성일을 기준으로 상위 5개의 글을 표시하며 메인페이지에 노출되는 게시판은 관리자 권한을 가진 회원으로 로그인할 경우 추가 및 삭제가 가능합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141781902-d53f3f9e-0f25-4ed6-a5a8-6b3fc85b05e2.png" width="90%" height="90%"></p>

> &#128276; 아이콘을 클릭하면 도착한 알림에 대한 목록을 확인할 수 있으며 알림 클릭시 확인 처리되어 알림 갯수가 차감됩니다.  
> **[알림 종류]**
> 1. 팔로잉한 회원이 게시글을 작성한 경우 알림을 받습니다.
> 2. 작성한 게시글에 댓글이 달린경우 알림을 받습니다.
> 3. 작성한 게시글을 다른 회원이 추천한 경우 알림을 받습니다.
> 4. 작성한 댓글을 다른 회원이 추천한경우 알림을 받습니다.
>
> **[알림 취소]**
> 1. 팔로잉한 회원이 게시글을 삭제시 받은 알림이 삭제됩니다.
> 2. 작성한 게시글에 댓글이 삭제될 경우 받은 알림이 삭제됩니다.
> 3. 다른 회원이 추천한 게시글의 추천 취소시 받은 알람이 삭제됩니다.
> 4. 다른 회원이 추천한 댓글의 추천 취소시 받은 알람이 삭제됩니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141611750-084f4d1c-10f2-46eb-9833-b8e78f011b21.png" width="90%" height="90%"></p>


<div style="page-break-after: always;"></div>

> 회원명에 커서를 가져가면 내 정보, 정보수정, 로그아웃이 표시됩니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141611774-856103f2-db7e-4f74-a52c-74d23adcd89d.png"></p>

**5. 내 정보**
> 회원이 CODEMOIM에서 활동한 내역에 대해서 확인할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/142190140-78507ebe-3483-4031-b000-98982b35db9e.png"></p>

<div style="page-break-after: always;"></div>

> 회원의 프로필 이미지와 기여도, 팔로잉 수, 팔로워 수, 팔로잉 태그의 수, 작성한 게시물 개수, 작성한 댓글 개수, 스크랩한 게시물 개수를 확인할 수 있습니다. 팔로우 클릭시 해당 회원을 팔로잉합니다.
>
> **[기여도 증감 정책]**
> 1. 질문 작성시 10점 증가
> 2. 답변 작성시 5점 증가
> 3. 작성한 게시글, 댓글을 다른 회원이 추천시 1점 증가
> 4. 작성한 게시글, 댓글을 다른 회원이 비추천시 1점 감소
> 5. 작성한 게시글을 다른 회원이 스크랩시 1점 증가
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141672791-3188af19-ed1e-419e-843a-59f4d459bd2a.png"></p>

<div style="page-break-after: always;"></div>

> **팔로잉**: 자신이 팔로잉한 회원을 목록을 출력합니다. 언팔로우 버튼 클릭시 팔로우가 해제됩니다.  
**팔로워**: 자신을 팔로우하고 있는 회원 목록을 출력합니다.  
**태그**: 자신이 팔로잉한 태그 목록을 출력합니다. 언팔로우 버튼 클릭시 팔로우가 해제됩니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141612121-8b5f46e9-b743-49b1-be4b-bb7cbf6b6355.png" width="50%" height="50%"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141612226-b3882f89-8389-4f21-b2ff-cb2eba862362.png" width="50%" height="50%"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141612244-903a227e-36c6-4cb7-bc5d-cab3477e17d8.png" width="50%" height="50%"></p>

<div style="page-break-after: always;"></div>

> **포스트**: 작성한 게시글 목록을 출력합니다.  
**댓글**: 작성한 댓글이 있는 게시글 목록을 출력합니다.  
**스크랩**: 스크랩한 게시글 목록을 출력합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141612413-62633a1b-5bae-48dc-a709-7085472c784e.png" width="60%" height="60%"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141612510-a1af2e02-cd75-4d5c-a99c-149a3a1c5109.png" width="60%" height="60%"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141612536-1e0b8cd3-2ce3-49fb-8245-8dd20056ba2c.png" width="60%" height="60%"></p>

**6. 정보수정**
> 프로필 이미지, 프로필 이름, 이메일을 수정할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141705506-bf5e3792-235b-4b91-8afb-9edcf6c22312.png"></p>

> 이메일 인증이 완료되지 않은경우 인증메일 전송을 클릭하여 이메일 인증을 위한 메일을 받을 수 있고 전송받은 메일에서 인증을 진행할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141705548-206bbdbf-0bb0-43cd-84a1-cbe35322cdf2.png"></p>

> 이메일 인증을 완료한 경우 인증메일 전송 버튼은 표시되지 않고 만약 이메일을 수정하는 경우 수정된 이메일로 다시 인증을 진행해야 합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141613075-22657d2f-9864-4e3b-b6b8-b8faa07d5422.png"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141612915-475ae5d6-725b-4f1b-afe6-ae84d5f6dcac.png"></p>

**7. 비밀번호 변경**
> 기존 비밀번호를 새로운 비밀번호로 변경합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141613119-f0edee2f-fbd2-4e6e-8c80-d5b024ebaf4e.png"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141612991-5468b639-afae-4c91-879b-004277d84caa.png"></p>

**8. 회원탈퇴**
> CODEMOIM 회원을 탈퇴합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141613140-c233dd6f-a5d9-464f-94f8-07234344d6d1.png" width="80%" height="80%"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141613160-5d168954-9fa0-43cc-94ac-bc9609bfc78f.png" width="50%" height="50%"></p>

**9. 검색**
> 전체 게시판의 게시글의 제목과 내용중 검색 키워드와 일치하는 게시글을 검색하여 목록을 출력합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141673347-b644b95c-dc1a-4380-bce2-b7b594ecbffe.png" width="90%" height="90%"></p>

**10. 게시판**
> 클릭한 게시판에 작성된 글의 목록을 출력합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141614587-a1d7b44a-1b6b-46dd-9e1e-20269d25051b.png" width="90%" height="90%"></p>

> **읽은 글**: 게시판에서 조회한 게시글의 목록을 출력합니다. 제목에 커서를 가져가면 전체 제목이 표시됩니다. 오른쪽 위의 휴지통 아이콘을 클릭하면 읽은 글 목록을 초기화합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141614674-de9246ef-4168-4007-b58d-24d2084fea77.png" width="25%" height="25%"></p>

> **팔로우 태그**: 회원이 팔로우한 태그의 목록을 출력합니다. 해당 영역은 로그인한 경우에만 활성화됩니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141614769-2704ec32-d4df-4b29-83bf-7a4c92a969f9.png" width="30%" height="30%"></p>

> **최신글**: 가장 최근에 작성된 글을 기준으로 게시글 목록을 출력합니다.  
**인기글**: 조회수가 많은 글을 기준으로 게시글 목록을 출력합니다.  
**최신글**: 추천수가 많은 글을 기준으로 게시글 목록을 출력합니다.  
**&#128270; 아이콘**: 검색창을 활성화/비활성화합니다.  
**&#10133; 아이콘**: 새로운 게시글을 작성합니다.  
**검색창**: 검색 키워드를 입력후 검색시 현재 게시판내의 게시물 중에서 일치하는 게시물을 검색합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141614878-04256e38-4e67-4da7-b5d0-c88a98bbd1e3.png" width="50%" height="50%"></p>

> **유저 순위**: 회원의 기여도를 기준으로 순위를 출력합니다.  
**태그 순위**: 태그 순위를 게시글/팔로워 기준으로 순위를 출력합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/173389652-24fada34-7741-48ae-be26-c2040e5a463f.png"></p>

<div style="page-break-after: always;"></div>

**11. 글 작성**
> 게시판에 올릴 게시물을 작성합니다.  
**게시판**: 게시물을 올릴 게시판을 선택할 수 있습니다.  
**제목**: 게시물의 제목을 작성합니다.  
**태그**: 게시물과 관련된 태그를 지정할 수 있습니다. 목록에 있는 태그를 다중 선택할 수 있고 없는경우 새로운 태그를 작성한후 엔터를 치면 새로운 태그가 생성됩니다.  
**본문**: 글 작성, 소스코드 작성, 이미지 업로드를 통해 게시글을 작성할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/142091872-9a38868e-9968-40a0-b1a5-337631a41207.png" width="80%" height="80%"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/142091820-c4778552-b1b7-4bcb-8df7-4e674a9650c1.png" width="55%" height="55%"></p>

<div style="page-break-after: always;"></div>

**12. 게시글 조회**
> 게시글 내용 및 댓글을 확인할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141644263-26ac6897-e544-44bc-bcc5-700e9e2a4ea9.png"></p>

> 🏷️: 게시글 작성시 사용한 태그들을 표시합니다. 게시판 라벨은 자동으로 할당됩니다.  
&#9726;&#9726;&#9726;: 해당 게시글을 작성한 회원이 게시글을 열람시 활성화 되는 아이콘이며 클릭시 수정, 삭제 버튼이 나타납니다.  
&#128065;: 게시글의 조회수를 표시합니다.  
&#128172;: 게시글의 댓글수를 표시합니다.  
&#128077;: 게시글을 추천합니다. 게시글을 비추천시 해당 아이콘은 비활성화 됩니다.  
&#128078;: 게시글을 비추천합니다. 게시글을 추천시 해당 아이콘은 비활성화 됩니다.  
&#128278;: 게시글의 스크랩수를 표시합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141664426-7b04d781-980c-434b-8c72-c69a0b0d43a7.png"></p>

> 댓글 목록을 출력합니다. 게시글을 작성한 회원의 경우 댓글 목록에서 댓글을 채택할 수 있는 버튼이 활성화됩니다. 댓글을 작성한 회원의 경우 해당 댓글에 &#9726;&#9726;&#9726; 아이콘이 활성화되어 댓글을 수정, 삭제할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141672610-00350882-fe36-4b42-b41d-9b9023a264e1.png" width="70%" height="70%"></p>

**13. 태그**
> 태그와 관련된 정보를 확인할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141673029-3c9cbd81-eac7-47c2-8987-562920104c7c.png"></p>

<div style="page-break-after: always;"></div>

> 태그의 팔로워, 태그가 사용된 게시글의 수를 표시하고 태그를 팔로우/언팔로우 합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141673086-07909779-9739-47f7-950d-66771ec3b06e.png"></p>

> **최신글**: 가장 최근에 작성된 글을 기준으로 게시글 목록을 출력합니다.  
**인기글**: 조회수가 많은 글을 기준으로 게시글 목록을 출력합니다.  
**최신글**: 추천수가 많은 글을 기준으로 게시글 목록을 출력합니다.  
**&#128270; 아이콘**: 검색창을 활성화/비활성화합니다.   
**검색창**: 검색 키워드를 입력후 검색시 현재 태그가 사용된 게시물 중에서 일치하는 게시물을 검색합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141673178-547cafd8-bbfa-4675-b813-b45dd0acb66b.png"></p>

<div style="page-break-after: always;"></div>

**14. 관리자 페이지**
> 관리자 권한을 가진 계정으로 로그인시 관리자 페이지에 접속할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141675027-020b2588-c2ae-4ec4-8396-6f1de0bfdd92.png"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/173071454-e3c62847-331f-4332-a902-9d5e455cc777.png"></p>

> 관리자 페이지는 게시판 관리, 태그 관리로 구성되어있습니다.  
게시판 관리 페이지에서는 게시판 목록 확인 및 수정, 삭제를 할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141675081-5fc40e07-cef9-4609-823f-49434991e4ab.png"></p>

<div style="page-break-after: always;"></div>

> 전체 게시판 목록을 출력합니다. 게시판 선택시 게시판 정보가 표시됩니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141675545-5e9ec973-2449-49f9-bda6-4393df411361.png"></p>

> 게시판 목록에서 선택한 게시판의 정보를 표시합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141675531-42c7d8da-e234-4eaf-95eb-6dda0e3644da.png" width="50%" height="50%"></p>

<div style="page-break-after: always;"></div>

> 태그 관리페이지에서는 태그의 목록 확인 및 수정, 삭제를 할 수 있습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141675578-3dff0416-4ad6-492c-b02f-d1283f26bbb9.png"></p>

> 전체 태그 목록을 출력합니다. 태그 선택시 태그 정보가 표시됩니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141675840-79007d62-b5f6-4527-a6b0-4b8566f34fb7.png" width="30%" height="30%"></p>

<div style="page-break-after: always;"></div>

> 태그 목록에서 선택한 태그의 정보를 표시합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/141675920-6649a6f1-59ec-4572-94e4-7de3bce554a1.png"></p>

<br/>

---

<div style="page-break-after: always;"></div>

### 4. 프로젝트 구조
> 해당 프로젝트에서 **기획, 설계, 프론트 엔드, 백엔드, 배포+인프라**를 담당했습니다.

**[기획]**
> - 프로젝트에서 구현하려는 기능에 대해서 정리한 후 해당 내용을 바탕으로 화면 정의서를 작성하였습니다.
> - 화면 정의서를 바탕으로 프론트 엔트 UI를 구현하였습니다.

**회원 정보 화면 정의서**
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146633314-86067bca-ee5c-48c4-88a8-4c4d12400a03.jpg" width="55%" height="55%"></p>

**회원 정보 구현화면**
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146633426-9e5743e4-8bcf-43cc-a9f6-41c3288d4e66.png" width="65%" height="65%"></p>

<div style="page-break-after: always;"></div>

**게시판 화면 정의서**
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146633471-1bfe4372-60e0-4f70-9d6b-db029fa723ee.jpg"></p>

<div style="page-break-after: always;"></div>

**게시판 구현화면**
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146633526-5a68ffc9-583c-4aa4-9963-02a78202a752.png" width="85%" height="85%"></p>

**[설계]**
> - 프로젝트에서 구현하려는 기능을 토대로 Table 생성 및 컬럼을 할당하였으며 각 테이블간의 관계를 ERDCloud를 사용하여 정의하였습니다.
> - ERDCloud: https://www.erdcloud.com/d/dYJj3cZNiRQPibkwN
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147316080-f573a2e1-6ed4-4449-b4d4-927bb0a1a9ed.png" width="85%" height="85%"></p>

<div style="page-break-after: always;"></div>

**[프론트 엔드]**
- React를 사용한 SPA(Single Page Application)
- Redux를 사용한 전역 state 관리(Redux Toolkit)
- React Router를 사용한 컴포넌트 이동
- Axios를 통한 서버 API 요청
- Ant Design을 사용한 컴포넌트 디자인
- PWA(Progressive Web Application) 적용
- Webpack을 사용한 프로젝트 빌드

**1. React를 사용한 SPA(Single Page Application)**
> - 함수형 컴포넌트를 사용하여 컴포넌트를 작성하였습니다.
> - Hooks를 사용해서 각각의 컴포넌트의 state 생성 및 사용, Redux의 state 사용(useSelector) 및 변환(useDispatch)을 수행하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146515096-715359a5-d870-408e-8e08-251064b81e7d.png"></p>

<div style="page-break-after: always;"></div>

**2. Redux를 사용한 전역 state 관리(Redux Toolkit)**
> - Redux Toolkit을 사용하여 state, reducer, extraReducers를 정의하여 사용하였습니다.
> - 비동기 요청의 경우 thunk를 사용하여 처리하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146515234-894b4d4d-a565-42af-965a-25514cafe515.png" width="60%" height="60%"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146534358-6c856c1b-66e5-4f64-baf0-58afa2643e96.png" width="85%" height="85%"></p>

<div style="page-break-after: always;"></div>

**3. React Router를 사용한 컴포넌트 이동**
> - React Routet Dom을 사용하여 Path에 해당하는 컴포넌트로 이동합니다.
> - 권한이 필요한 컴포넌트로 이동하는 경우 권한 체크를 수행하는 PrivateRoute를 통과하는 경우 이동이 되도록 구성하였습니다.

<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146515486-e9bf61f2-4539-4dff-9162-ac861b0e2ae4.png"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146515780-7223e747-db87-4c35-8501-4c4c466353bb.png"></p>

<div style="page-break-after: always;"></div>

**4. Axios를 통한 서버 API 요청**
> - 서버로 API 요청시 Axios를 사용하여 요청을 처리하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146536108-6a715590-aaa8-4c24-bb07-0e8abc69bc0e.png" width="75%" height="75%"></p>

**5. Ant Design을 사용한 컴포넌트 디자인**
> - 컴포넌트의 UI를 Ant Design을 사용하여 구현하였습니다.(Grid, Layout, Button, Table...)
> - Grid의 경우 각 컴포넌트에서 표현할 화면 구성에 따라서 Row 개수를 조절하여 선언하고 Row 내부에서 Col을 선언하여 Row 내부에서 표현될 영역을 표시하였습니다.
> - Layout의 경우 Header, Sider, Breadcrumb, Main, Footer 5가지 영역으로 나누어서 구성하였습니다.

**Grid 사용**
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146537000-2bb22228-45b9-4f52-ba58-83955c4e4583.png"></p>

**Layout 사용**
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146537542-8b700ada-83c5-4947-8716-994860accbb7.png" width="75%" height="75%"></p>

**6. PWA(Progressive Web Application) 적용**
> - 모바일에서 사용시 편의를 위해 PWA를 적용하였습니다.
> - manifest.json 파일은 Webpack 플러그인(WebpackPwaManifest)을 통해 빌드시 생성되도록 설정하였습니다.
> - Service Worker 파일의 경우 별도로 파일을 생성한 뒤 load 이벤트 발생시 등록되도록 설정하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146537656-4cc3ea89-beb0-47d7-b5dd-8b11b91d6ffa.png"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146537777-1d53332d-0368-47f8-8c6a-09c1e96cff53.png"></p>

**7. Webpack을 사용한 프로젝트 빌드**
> - Webpack 번들러를 사용하여 프로젝트 빌드하였습니다.
> - Webpack 파일을 dev, prod 두 가지로 분류하여 개발 환경과 배포 환경을 구분하여 사용하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/146537992-96b30b77-ef9d-4bc5-9601-060adfe9bf67.png"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/173405044-e3499a11-f5c1-4634-b518-86e28ede2934.png"></p>

<div style="page-break-after: always;"></div>

**[백엔드]**
- Spring Boot
- Spring Security
- Oauth2
- JWT
- JPA & QueryDSL
- Hibernate Search6 + nori 한글 형태소 분석기
- RabbitMQ + HAProxy + Keepalived
- Junit

**1. Spring Boot**
> 백엔드 서버 개발에 Spring Boot를 사용하여 개발을 진행하였습니다.

**[의존성 주입(생성자 주입 방식)]**
> DI(Dependency Injection) 수행시 생성자 주입 방식을 사용하여 DI(Dependency Injection)를 수행하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147028202-8d815d80-d252-4d53-bb53-405c854963fe.png"></p>

**[예외처리]**
> @RestController를 선언한 클래스에서 로직 수행중 발생하는 에러에 대한 예외처리를 @RestControllerAdvice를 선언한 CustomRestControllerAdvice에서 캐치하여 처리할 수 있도록 하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147028491-2ecc75ab-540c-4063-bbdf-3e3247fc721d.png" width="90%" height="90%"></p>

<div style="page-break-after: always;"></div>

> 예외에 대한 로그를 상세히 볼 수 있도록 설정하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147029311-0197d557-c966-4170-9865-9936f77b9ccb.png"></p>

**[AOP 사용]**
> 로그인 권한이 필요한 API 요청의 경우 토큰에 대한 검증이 필요하므로 이에 대한 검증 로직이 필요합니다. 토큰 검증 로직의 경우 같은 로직이 여러번 사용되기 때문에 AOP를 사용하여 공통적으로 사용되는 로직을 분리합니다. @AccessTokenUse 어노테이션을 생성해서 토큰 검증 로직이 필요한 영역에 선언하여 사용할 수 있도록 하였습니다.

> 토큰 검증 로직이 필요한 메서드에 선언할 어노테이션을 @AccessTokenUse으로 선언해서 사용하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147033620-013a7dba-7ca5-4558-9928-f2c1c50ff4fc.png"></p>
> @AccessTokenUse가 선언된 메서드에서 토큰 검증 로직이 정상적으로 처리되는 경우 Long 타입의 userId 파라미터가 추가되게 됩니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147033700-d58ad52a-3d00-4de7-a7eb-eff2cbcc055c.png"></p>

**2. Spring Security**
> Spring Securtiy를 사용하여 로그인 처리 및 권한이 필요한 요청에 대한 권한 체크를 수행하였습니다.

> **[로그인 처리 필터]**  
> LoginAuthenticationFilter: 로그인 URL로 요청이 오는경우 해당 필터에서 로그인 처리 및 토큰 생성을 수행합니다.

> **[권한 검증 필터]**  
> JwtAuthenticationFilter: 권한이 필요없는 요청의 경우 해당 필터에서 제외되며 권한이 필요한 요청의 경우 해당 필터에서 토큰을 조회 및 검증하여 해당 요청에 대해 처리합니다.

<div style="page-break-after: always;"></div>

**3. Oauth2**
> Oauth2를 사용하여 GitHub, Kakao, Naver의 계정을 사용해 로그인을 할 수 있도록 하였습니다. Oauth2의 Authorization Code Grant 인증방식을 사용하지만 Backend Server에서 Token의 관리 및 갱신을 관리하기 위하여 Resource Server에 Access Token을 생성 요청을 하지않고 Client(Backend Server)에서 Access Token을 생성하여 발급하는 구조로 개발하였습니다.

<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147182798-f5bf4a64-5a2b-4f45-8a9c-d850f1ad5f12.png"></p>

**4. JWT**
> 로그인 성공시 발급되는 토큰을 JWT로 생성한 뒤 쿠키에 저장하여 발급하였습니다. 보안을 위해 JWT를 저장하는 쿠키는 HttpOnly, Secure를 설정 및 SameSite를 Lax 설정 & CSRF 토큰을 사용하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/186385022-0900970e-c9f0-40a2-a271-de406e0131a7.PNG"></p>

**5. JPA & QueryDSL**
> JPA를 사용하여 간단한 조회 쿼리 및 CRUD 쿼리를 별도로 작성하지 않고 처리하였으며 그 외의 쿼리는 QueryDSL을 사용하여 쿼리를 작성하였습니다.(컴파일 시점에서 오류탐지, 동적쿼리 작성등의 이점)

**6. Hibernate Search6 + nori 한글 형태소 분석기**
> 검색 속도 개선을 위해 Full-text Search 기능을 지원하는 Hibernate Search6를 사용하였고 Full-text Search를 위한 인덱싱 작업시 한글에 대한 인덱싱을 위해서 nori 한글 형태소 분석기를 Hibernate Search6의 analyzer로 설정하여 사용하였습니다.

> 100만건의 게시물 테스트 데이터를 생성하여 MySQL의 like를 사용하여 검색한 경우와 Hibernate Search를 사용하여 검색한 경우를 비교하였을때 약 8.6배 정도 검색 속도가 차이나는 것을 확인하였습니다.

<div style="page-break-after: always;"></div>

**=> MySQL의 like를 사용한 테스트 결과**
테스트를 통해 검색시 소요되는 시간을 측정합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147633026-608644f0-45ab-4adc-b7db-07f654bf760a.png"></p>

MySQL의 like를 사용하여 100만건의 게시물에서 테스트를 키워드로 검색시 4025ms의 시간이 소요되었습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147633286-5ad76028-3163-40a5-a9b0-15951791e087.png"></p>

**=> Hibernate Search 사용한 테스트 결과**
어플리케이션 동작시에 인덱싱 대상인 100만건의 게시물에 대한 인덱싱을 수행합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147631115-da53e4d2-43ff-4331-b7af-2f206d6b8df7.png"></p>

테스트를 통해 검색시 소요되는 시간을 측정합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147631397-65fecd2b-e828-4cd9-9f44-31e6658ecf28.png"></p>

Hibernate Search를 사용하여 100만건의 게시물에서 테스트를 키워드로 검색시 468ms의 시간이 소요되었습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/147633444-a07ef958-89f3-452c-a30f-f6fae829d37b.png"></p>

<div style="page-break-after: always;"></div>

**7. RabbitMQ + HAProxy + Keepalived**
> RabbitMQ를 사용하여 회원의 활동에 의해 생성되는 알림을 실시간으로 다른 회원에게 전달합니다.

**[알림 종류]**
1. 팔로잉한 회원이 게시글을 작성한 경우 알림을 받습니다.
2. 작성한 게시글에 댓글이 달린경우 알림을 받습니다.
3. 작성한 게시글을 다른 회원이 추천한 경우 알림을 받습니다.
4. 작성한 댓글을 다른 회원이 추천한경우 알림을 받습니다.

<p align="center"><img src="https://user-images.githubusercontent.com/73155105/149614823-fae00bc9-4447-4dc6-b259-31f9e18e9e15.png"></p>

<div style="page-break-after: always;"></div>

**[구성]**  
RabbitMQ 서버는 Master 1대, Slave 2대를 생성하여 Master 서버에서 장애가 발생하는 경우 Slave가 Master로 승격되도록 클러스터링 하였으며 3대의 서버에서 Queue가 모든 서버에 서로 복제되어 저장될 수 있도록 미러링을 구성하였습니다. 별도의 서버에서 HAProxy를 사용하여 3대의 RabbitMQ 서버의 상태체크 및 로드밸런싱을 수행하였고 SPOF(Single Point of Failure) 방지를 위해서 두 대의 서버에 Keepalived를 설치하여 한 대의 서버가 중지되어도 다른 한 대의 서버에서 해당 작업을 수행할 수 있도록 이중화 구성을 하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/171528245-4b36052d-8de0-4bad-ad89-f769da89ea34.png"></p>
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/171528271-9a437d52-385a-4aed-8e78-9397379e1d7e.png"></p>

<div style="page-break-after: always;"></div>

**8. Junit**
> 기능 개발을 하면서 생성한 Service, Repository의 메서드(212개)를 테스트 하였습니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/149615240-f8ef63b3-2d3a-4e51-b95c-0e6cfe5daae7.png"></p>

<div style="page-break-after: always;"></div>

#### [배포+인프라]
- AWS EC2
- AWS S3
- AWS Cloudfront
- AWS Route53
- AWS Parameter Store
- AWS ELB(Application Load Balancer)
- AWS Auto Scaling
- AWS CodeDeploy
- AWS CloudWatch
- Jenkins
- Nginx

**1. 배포**  
(1) 사용자가 Git에서 push를 수행하는 경우 Github의 Webhook에 의해서 Jenkins 서버에서 해당 이벤트를 전달받아 Build를 수행하고 Build가 완료되면 S3 버킷에 Build 결과물을 저장하고 CodeDeploy에 배포를 요청합니다.

CodeDeploy는 배포를 생성하여 배포 대상인 EC2에 배포를 시작하고 EC2는 배포에 필요한 Build 파일을 S3로부터 다운받아 사용하고 Application 구동시 필요한 파라미터들을 Parameter Store에서 읽어와서 사용합니다.
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/172576802-0898b0ed-3934-4aeb-9bff-8ad3a0eafdec.png"></p>

(2) Auto Scaling에서 용량을 증가하게 되면 인스턴스가 증가한 갯수만큼 생성되고 Auto Scaling에 설정되어 있는 Life Cycle Hook에 의해서 인스턴스 생성에 대한 알림이 CodeDeploy로 전달되게 됩니다. CodeDeploy는 알림을 받은 후 새롭게 생성된 인스턴스를 대상으로 배포를 시작합니다.

<div style="page-break-after: always;"></div>

<p align="center"><img src="https://user-images.githubusercontent.com/73155105/174052121-d779ee52-5831-4888-9c85-54e4ba95f144.png" width="80%" height="80%"></p>

**2. 인프라**  
(1) AWS S3를 사용한 이미지 저장  
(2) AWS Cloudfront를 통해서만 AWS S3 이미지에 접근하도록 제한  
(3) AWS Route53을 사용한 도메인 등록 및 Elastic Load Blancing에 도메인 할당  
(4) AWS CloudWatch를 사용한 Application 로그 저장  
(5) AWS Elastic Load Balancing(Application Load Balancer)을 사용한 부하 분산  
(6) AWS Auto Scaling을 사용한 인스턴스 용량 자동조정  
(7) Web Server에 NAT Instance 연결  
(8) AWS EC2 인스턴스, AWS RDS를 Private Subnet에 구성  
(9) AWS RDS를 사용한 MySQL Database 생성 및 사용  
(10) AWS RDS를 사용한 Multi-AZ, Read Replica 구성  
(11) AWS Route53의 가중치 기반 라우팅을 통한 Read Replica의 부하 분산  
(12) Nginx를 사용한 Reverse Proxy 사용 및 캐싱, 응답시간, 요청제한 설정
<p align="center"><img src="https://user-images.githubusercontent.com/73155105/173564795-78bb83ae-9c63-4be8-be59-ac5d168dcd03.PNG"></p>