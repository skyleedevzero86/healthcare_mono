class HealthcareApp {
    constructor() {
        this.currentUser = null;
        this.isAuthenticated = false;
        this.currentScreen = 'dashboard';
        this.healthData = [];
        this.communityPosts = [];
        this.healthScore = null;
        this.currentPage = 0;
        this.postsPerPage = 5;
        this.isLoadingMore = false;
        this.healthCurrentPage = 0;
        this.healthPostsPerPage = 5;
        this.isLoadingMoreHealth = false;
        
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadDummyData();
        this.loadFromStorage();
        this.showLoadingScreen();
        
        setTimeout(() => {
            this.hideLoadingScreen();
            this.checkAuthentication();
        }, 2000);
    }

    setupEventListeners() {
        document.getElementById('show-signup').addEventListener('click', (e) => {
            e.preventDefault();
            this.showSignupScreen();
        });

        document.getElementById('back-to-login').addEventListener('click', () => {
            this.showLoginScreen();
        });

        document.getElementById('login-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });

        document.getElementById('signup-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleSignup();
        });

        document.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const screen = e.currentTarget.dataset.screen;
                this.navigateToScreen(screen);
            });
        });

        document.getElementById('refresh-btn').addEventListener('click', () => {
            this.refreshData();
        });

        document.querySelectorAll('.chart-button').forEach(button => {
            button.addEventListener('click', (e) => {
                const chartType = e.currentTarget.dataset.chart;
                this.showChart(chartType);
            });
        });

        document.getElementById('show-health-input').addEventListener('click', () => {
            this.showHealthModal();
        });

        document.getElementById('close-health-modal').addEventListener('click', () => {
            this.hideHealthModal();
        });

        document.getElementById('cancel-health').addEventListener('click', () => {
            this.hideHealthModal();
        });

        document.getElementById('health-modal').addEventListener('click', (e) => {
            if (e.target.id === 'health-modal') {
                this.hideHealthModal();
            }
        });

        document.getElementById('submit-health-data').addEventListener('click', () => {
            this.submitHealthData();
        });

        document.getElementById('show-write-form').addEventListener('click', () => {
            this.showWriteModal();
        });

        document.getElementById('close-write-modal').addEventListener('click', () => {
            this.hideWriteModal();
        });

        document.getElementById('cancel-post').addEventListener('click', () => {
            this.hideWriteModal();
        });

        document.getElementById('write-modal').addEventListener('click', (e) => {
            if (e.target.id === 'write-modal') {
                this.hideWriteModal();
            }
        });

        document.getElementById('submit-post').addEventListener('click', () => {
            this.submitPost();
        });

        document.getElementById('mini-logout-btn').addEventListener('click', () => {
            this.handleLogout();
        });

        document.getElementById('edit-profile').addEventListener('click', () => {
            this.showEditProfileModal();
        });

        document.getElementById('change-password').addEventListener('click', () => {
            this.showChangePasswordModal();
        });

        document.getElementById('close-modal').addEventListener('click', () => {
            this.hideChart();
        });

        document.getElementById('chart-modal').addEventListener('click', (e) => {
            if (e.target.id === 'chart-modal') {
                this.hideChart();
            }
        });

        document.getElementById('close-edit-profile-modal').addEventListener('click', () => {
            this.hideEditProfileModal();
        });

        document.getElementById('cancel-edit-profile').addEventListener('click', () => {
            this.hideEditProfileModal();
        });

        document.getElementById('edit-profile-modal').addEventListener('click', (e) => {
            if (e.target.id === 'edit-profile-modal') {
                this.hideEditProfileModal();
            }
        });

        document.getElementById('save-profile-changes').addEventListener('click', () => {
            this.saveProfileChanges();
        });

        document.getElementById('close-change-password-modal').addEventListener('click', () => {
            this.hideChangePasswordModal();
        });

        document.getElementById('cancel-change-password').addEventListener('click', () => {
            this.hideChangePasswordModal();
        });

        document.getElementById('change-password-modal').addEventListener('click', (e) => {
            if (e.target.id === 'change-password-modal') {
                this.hideChangePasswordModal();
            }
        });

        document.getElementById('save-password-changes').addEventListener('click', () => {
            this.savePasswordChanges();
        });

        document.getElementById('close-post-detail-modal').addEventListener('click', () => {
            this.hidePostDetailModal();
        });

        document.getElementById('post-detail-modal').addEventListener('click', (e) => {
            if (e.target.id === 'post-detail-modal') {
                this.hidePostDetailModal();
            }
        });

    }

    loadDummyData() {
        if (!this.currentUser) {
            this.currentUser = {
                userSeq: 1,
                userId: 'user123',
                userNm: '홍길동',
                userRoleFk: 'USER',
                email: 'user@example.com',
                telNumEnc: '010-1234-5678',
                birthEnc: '1990-01-01',
                gender: 'M',
                height: 175,
                weight: 70,
                bloodType: 'A'
            };
        }

        if (this.healthData.length === 0) {
            this.healthData = [];
            
            for (let i = 0; i < 25; i++) {
                const daysAgo = i;
                const baseTime = new Date(Date.now() - daysAgo * 24 * 60 * 60 * 1000);
                
                this.healthData.push({
                    userId: 'user123',
                    time: baseTime.toISOString(),
                    heartrate: Math.floor(Math.random() * 30) + 60,
                    temperature: Math.round((Math.random() * 2 + 35.5) * 10) / 10,
                    spo2: Math.floor(Math.random() * 5) + 95,
                    step: Math.floor(Math.random() * 5000) + 5000,
                    stress: Math.floor(Math.random() * 5) + 1,
                    bloodpressMin: Math.floor(Math.random() * 20) + 70,
                    bloodpressMax: Math.floor(Math.random() * 30) + 110,
                    repiratory: Math.floor(Math.random() * 8) + 12,
                    sleep: Math.round((Math.random() * 4 + 6) * 10) / 10
                });
            }
            
            this.healthData.sort((a, b) => new Date(b.time) - new Date(a.time));
        }

        if (!this.healthScore) {
            this.healthScore = {
                userId: 'user123',
                dailyDate: new Date().toISOString().split('T')[0],
                userSleepScore: 90,
                userExerciseScore: 80,
                userStressScore: 85,
                healthScore: 85
            };
        }

        if (this.communityPosts.length === 0) {
            this.communityPosts = [];
            const users = [
                { userId: 'user123', userNm: '홍길동' },
                { userId: 'user456', userNm: '김영희' },
                { userId: 'user789', userNm: '이철수' },
                { userId: 'user101', userNm: '박민수' },
                { userId: 'user202', userNm: '정수진' },
                { userId: 'user303', userNm: '최영호' },
                { userId: 'user404', userNm: '한지영' },
                { userId: 'user505', userNm: '강태현' }
            ];
            
            const contents = [
                '오늘 아침 운동을 하고 나서 기분이 정말 좋네요! 심박수도 정상 범위에 있고 체온도 좋습니다.',
                '수면 시간을 늘리고 나서 건강 점수가 많이 올랐어요. 8시간 수면의 효과가 정말 대단하네요.',
                '스트레스 관리가 중요하다는 걸 다시 한번 느꼈습니다. 명상과 요가를 시작해보려고 해요.',
                '요즘 물을 많이 마시고 있는데, 피부가 좋아진 것 같아요. 수분 섭취의 중요성을 실감합니다.',
                '걷기 운동을 시작한 지 한 달이 되었는데, 체력이 많이 좋아진 것 같아요.',
                '건강한 식단을 유지하려고 노력하고 있는데, 야채 섭취량을 늘려야겠어요.',
                '명상 앱을 사용해보니 마음이 편안해지는 것 같습니다. 추천드려요!',
                '규칙적인 운동이 얼마나 중요한지 몸으로 느끼고 있습니다.',
                '건강 검진을 받았는데 모든 수치가 정상이에요. 기분이 좋습니다!',
                '새로운 운동 루틴을 만들어봤는데, 몸이 많이 개선된 것 같아요.',
                '건강한 간식으로 견과류를 먹고 있는데, 포만감도 좋고 영양도 좋네요.',
                '수면 패턴을 개선하니 하루 종일 컨디션이 좋아졌어요.',
                '스트레칭을 매일 하기 시작했는데, 몸이 훨씬 유연해진 것 같아요.',
                '건강한 아침 식사를 챙기고 있는데, 하루 에너지가 달라지는 것 같아요.',
                '걷기와 조깅을 병행하고 있는데, 체력이 많이 향상되었어요.'
            ];
            
            for (let i = 0; i < 20; i++) {
                const user = users[Math.floor(Math.random() * users.length)];
                const content = contents[Math.floor(Math.random() * contents.length)];
                const daysAgo = Math.floor(Math.random() * 30);
                
                this.communityPosts.push({
                    commuSeq: i + 1,
                    content: content,
                    regDate: new Date(Date.now() - daysAgo * 24 * 60 * 60 * 1000).toISOString(),
                    heartrate: Math.random() > 0.5 ? Math.floor(Math.random() * 40) + 60 : 0,
                    temperature: Math.random() > 0.5 ? Math.round((Math.random() * 2 + 35.5) * 10) / 10 : 0,
                    bloodpress: Math.random() > 0.5 ? Math.floor(Math.random() * 40) + 100 : 0,
                    smoking: 0,
                    drinking: 0,
                    exercise: Math.floor(Math.random() * 2),
                    age: Math.floor(Math.random() * 30) + 20,
                    userId: user.userId,
                    userNm: user.userNm,
                    bodyAge: Math.floor(Math.random() * 10) + 20
                });
            }
            
            this.communityPosts.sort((a, b) => new Date(b.regDate) - new Date(a.regDate));
        }
    }

    saveToStorage() {
        localStorage.setItem('healthcare_healthData', JSON.stringify(this.healthData));
        localStorage.setItem('healthcare_communityPosts', JSON.stringify(this.communityPosts));
        localStorage.setItem('healthcare_healthScore', JSON.stringify(this.healthScore));
        localStorage.setItem('healthcare_currentUser', JSON.stringify(this.currentUser));
    }

    loadFromStorage() {
        const savedHealthData = localStorage.getItem('healthcare_healthData');
        const savedCommunityPosts = localStorage.getItem('healthcare_communityPosts');
        const savedHealthScore = localStorage.getItem('healthcare_healthScore');
        const savedCurrentUser = localStorage.getItem('healthcare_currentUser');

        if (savedHealthData) {
            this.healthData = JSON.parse(savedHealthData);
        }
        if (savedCommunityPosts) {
            this.communityPosts = JSON.parse(savedCommunityPosts);
        }
        if (savedHealthScore) {
            this.healthScore = JSON.parse(savedHealthScore);
        }
        if (savedCurrentUser) {
            this.currentUser = JSON.parse(savedCurrentUser);
        }
    }

    showLoadingScreen() {
        document.getElementById('loading-screen').classList.remove('hidden');
    }

    hideLoadingScreen() {
        document.getElementById('loading-screen').classList.add('hidden');
    }

    checkAuthentication() {
        const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
        
        if (isLoggedIn) {
            this.isAuthenticated = true;
            this.showMainApp();
            this.updateDashboard();
        } else {
            this.showAuthScreen();
        }
    }

    showAuthScreen() {
        document.getElementById('auth-container').classList.remove('hidden');
        document.getElementById('main-container').classList.add('hidden');
        this.showLoginScreen();
    }

    showMainApp() {
        document.getElementById('auth-container').classList.add('hidden');
        document.getElementById('main-container').classList.remove('hidden');
        this.navigateToScreen('dashboard');
    }

    showLoginScreen() {
        document.getElementById('login-screen').classList.remove('hidden');
        document.getElementById('signup-screen').classList.add('hidden');
    }

    showSignupScreen() {
        document.getElementById('login-screen').classList.add('hidden');
        document.getElementById('signup-screen').classList.remove('hidden');
    }

    handleLogin() {
        const userId = document.getElementById('login-userId').value;
        const password = document.getElementById('login-password').value;

        if (!userId || !password) {
            this.showToast('아이디와 비밀번호를 입력해주세요.', 'error');
            return;
        }

        this.showButtonLoading('login-form');
        
        setTimeout(() => {
            this.hideButtonLoading('login-form');
            this.isAuthenticated = true;
            localStorage.setItem('isLoggedIn', 'true');
            this.showMainApp();
            this.updateDashboard();
            this.showToast('로그인 성공!', 'success');
        }, 1500);
    }

    handleSignup() {
        const formData = {
            userId: document.getElementById('signup-userId').value,
            password: document.getElementById('signup-password').value,
            name: document.getElementById('signup-name').value,
            email: document.getElementById('signup-email').value,
            birth: document.getElementById('signup-birth').value,
            phone: document.getElementById('signup-phone').value,
            gender: document.querySelector('input[name="gender"]:checked')?.value || '',
            bloodType: document.querySelector('input[name="bloodType"]:checked')?.value || '',
            agreement: document.getElementById('agreement').checked
        };

        if (!formData.userId || !formData.password || !formData.name || !formData.email) {
            this.showToast('필수 정보를 모두 입력해주세요.', 'error');
            return;
        }

        if (!formData.agreement) {
            this.showToast('개인정보 처리방침에 동의해주세요.', 'error');
            return;
        }

        this.showButtonLoading('signup-form');
        
        setTimeout(() => {
            this.hideButtonLoading('signup-form');
            this.showToast('회원가입이 완료되었습니다.', 'success');
            setTimeout(() => {
                this.showLoginScreen();
                this.showToast('로그인해주세요.', 'info');
            }, 1000);
        }, 2000);
    }

    handleLogout() {
        if (confirm('정말 로그아웃하시겠습니까?')) {
            this.isAuthenticated = false;
            localStorage.removeItem('isLoggedIn');
            this.showAuthScreen();
            this.showToast('로그아웃되었습니다.', 'info');
        }
    }

    navigateToScreen(screenName) {
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`[data-screen="${screenName}"]`).classList.add('active');

        document.querySelectorAll('.main-screen').forEach(screen => {
            screen.classList.add('hidden');
        });

        document.getElementById(`${screenName}-screen`).classList.remove('hidden');


        this.currentScreen = screenName;

        switch (screenName) {
            case 'dashboard':
                this.updateDashboard();
                break;
            case 'healthinfo':
                this.updateHealthInfo();
                break;
            case 'community':
                this.updateCommunity();
                break;
            case 'profile':
                this.updateProfile();
                break;
        }
    }

    updateDashboard() {
        if (!this.currentUser) return;

        document.getElementById('welcome-message').textContent = 
            `안녕하세요, ${this.currentUser.userNm}님!`;

        const latestHealth = this.healthData[0];
        if (latestHealth) {
            document.getElementById('heartrate-value').textContent = `${latestHealth.heartrate} bpm`;
            document.getElementById('temperature-value').textContent = `${latestHealth.temperature}°C`;
            document.getElementById('bloodpress-value').textContent = 
                `${latestHealth.bloodpressMax}/${latestHealth.bloodpressMin}`;
            document.getElementById('spo2-value').textContent = `${latestHealth.spo2}%`;
        }

        if (this.healthScore) {
            document.getElementById('total-score').textContent = this.healthScore.healthScore;
            document.getElementById('sleep-score').textContent = this.healthScore.userSleepScore;
            document.getElementById('exercise-score').textContent = this.healthScore.userExerciseScore;
            document.getElementById('stress-score').textContent = this.healthScore.userStressScore;
        }

        this.generateAIAdvice();
    }

    generateAIAdvice() {
        const loadingElement = document.getElementById('ai-loading');
        const adviceElement = document.getElementById('ai-advice-text');
        
        loadingElement.style.display = 'block';
        adviceElement.style.display = 'none';

        setTimeout(() => {
            const latestHealth = this.healthData[0];
            const advice = this.analyzeHealthData(latestHealth);
            
            loadingElement.style.display = 'none';
            adviceElement.style.display = 'block';
            adviceElement.innerHTML = advice;
        }, 2000);
    }

    analyzeHealthData(healthData) {
        if (!healthData) {
            return `
                <p>건강 데이터가 없습니다. 데이터를 입력해주세요.</p>
                <div class="ai-recommendation">
                    <strong>추천사항:</strong> 건강 정보 메뉴에서 데이터를 입력해보세요.
                </div>
            `;
        }

        const heartrateStatus = this.getHealthStatus(healthData.heartrate, 'heartrate');
        const temperatureStatus = this.getHealthStatus(healthData.temperature, 'temperature');
        const spo2Status = this.getHealthStatus(healthData.spo2, 'spo2');
        
        let analysis = '';
        let recommendations = [];
        let overallStatus = '양호';

        if (heartrateStatus.status === '높음') {
            analysis += '<li>심박수가 높은 편입니다. 스트레스나 과도한 운동이 원인일 수 있습니다.</li>';
            recommendations.push('충분한 휴식과 스트레스 관리가 필요합니다.');
            overallStatus = '주의';
        } else if (heartrateStatus.status === '낮음') {
            analysis += '<li>심박수가 낮은 편입니다. 운동 부족이나 저혈압을 의심해볼 수 있습니다.</li>';
            recommendations.push('가벼운 운동을 시작해보세요.');
            overallStatus = '주의';
        } else {
            analysis += '<li>심박수가 정상 범위에 있어 심혈관 건강이 좋습니다.</li>';
        }

        if (temperatureStatus.status === '높음') {
            analysis += '<li>체온이 높습니다. 감염이나 염증이 있을 수 있습니다.</li>';
            recommendations.push('충분한 수분 섭취와 휴식을 취하세요.');
            overallStatus = '주의';
        } else if (temperatureStatus.status === '낮음') {
            analysis += '<li>체온이 낮습니다. 면역력 저하나 대사 기능 저하를 의심해볼 수 있습니다.</li>';
            recommendations.push('영양가 있는 음식을 섭취하고 충분한 수면을 취하세요.');
            overallStatus = '주의';
        } else {
            analysis += '<li>체온이 정상이므로 감염 증상은 없어 보입니다.</li>';
        }

        if (spo2Status.status === '낮음') {
            analysis += '<li>산소포화도가 낮습니다. 호흡기 문제나 폐 기능 저하를 의심해볼 수 있습니다.</li>';
            recommendations.push('깊은 호흡 운동과 실내 환기를 자주 하세요.');
            overallStatus = '주의';
        } else {
            analysis += '<li>산소포화도가 양호하여 호흡 기능이 정상입니다.</li>';
        }

        if (healthData.step < 5000) {
            analysis += '<li>걸음수가 부족합니다. 신체 활동이 부족한 상태입니다.</li>';
            recommendations.push('하루 8,000보 이상 걷기를 목표로 하세요.');
        } else if (healthData.step > 12000) {
            analysis += '<li>걸음수가 충분합니다. 활발한 신체 활동을 하고 있습니다.</li>';
        } else {
            analysis += '<li>걸음수가 적당합니다. 꾸준한 활동을 유지하세요.</li>';
        }

        if (recommendations.length === 0) {
            recommendations.push('현재 상태를 유지하며 규칙적인 건강 관리를 계속하세요.');
        }

        const statusColor = overallStatus === '양호' ? '#4CAF50' : '#FF9800';
        const statusText = overallStatus === '양호' ? '전반적으로 양호한 상태' : '일부 주의가 필요한 상태';

        return `
            <p style="color: ${statusColor}; font-weight: bold; margin-bottom: 1rem;">
                최근 건강 데이터를 분석한 결과, ${statusText}입니다.
            </p>
            <ul>
                ${analysis}
            </ul>
            <div class="ai-recommendation">
                <strong>추천사항:</strong> ${recommendations.join(' ')}
            </div>
        `;
    }

    updateHealthInfo() {
        const container = document.getElementById('health-data-cards');
        
        if (this.healthCurrentPage === 0) {
            container.innerHTML = '';
        }

        if (this.healthData.length === 0) {
            container.innerHTML = `
                <div class="data-card">
                    <p>건강 데이터가 없습니다.</p>
                    <p>데이터를 입력해보세요!</p>
                </div>
            `;
            return;
        }

        const startIndex = this.healthCurrentPage * this.healthPostsPerPage;
        const endIndex = startIndex + this.healthPostsPerPage;
        const dataToShow = this.healthData.slice(startIndex, endIndex);

        dataToShow.forEach((data, index) => {
            const card = document.createElement('div');
            card.className = 'data-card';
            
            const heartrateStatus = this.getHealthStatus(data.heartrate, 'heartrate');
            const temperatureStatus = this.getHealthStatus(data.temperature, 'temperature');
            const spo2Status = this.getHealthStatus(data.spo2, 'spo2');

            card.innerHTML = `
                <div class="data-header">
                    <span class="data-time">${new Date(data.time).toLocaleString()}</span>
                </div>
                <div class="data-grid">
                    <div class="data-item">
                        <span class="data-label">심박수</span>
                        <span class="data-value ${heartrateStatus.class}">${data.heartrate} bpm</span>
                        <span class="data-status ${heartrateStatus.class}">${heartrateStatus.status}</span>
                    </div>
                    <div class="data-item">
                        <span class="data-label">체온</span>
                        <span class="data-value ${temperatureStatus.class}">${data.temperature}°C</span>
                        <span class="data-status ${temperatureStatus.class}">${temperatureStatus.status}</span>
                    </div>
                    <div class="data-item">
                        <span class="data-label">산소포화도</span>
                        <span class="data-value ${spo2Status.class}">${data.spo2}%</span>
                        <span class="data-status ${spo2Status.class}">${spo2Status.status}</span>
                    </div>
                    <div class="data-item">
                        <span class="data-label">걸음수</span>
                        <span class="data-value">${data.step.toLocaleString()}</span>
                    </div>
                </div>
            `;
            
            container.appendChild(card);
        });

        this.setupHealthInfiniteScroll();
    }

    setupHealthInfiniteScroll() {
        const healthContainer = document.getElementById('health-data-cards');
        const loadingMore = document.getElementById('loading-more-health');
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && !this.isLoadingMoreHealth) {
                    this.loadMoreHealthData();
                }
            });
        }, {
            threshold: 0.1
        });

        if (healthContainer.lastElementChild) {
            observer.observe(healthContainer.lastElementChild);
        }
    }

    loadMoreHealthData() {
        const totalData = this.healthData.length;
        const currentDisplayed = (this.healthCurrentPage + 1) * this.healthPostsPerPage;
        
        if (currentDisplayed >= totalData) {
            return;
        }

        this.isLoadingMoreHealth = true;
        const loadingMore = document.getElementById('loading-more-health');
        loadingMore.style.display = 'block';

        setTimeout(() => {
            this.healthCurrentPage++;
            this.updateHealthInfo();
            this.isLoadingMoreHealth = false;
            loadingMore.style.display = 'none';
        }, 500);
    }

    updateCommunity() {
        const container = document.getElementById('posts-container');
        
        if (this.currentPage === 0) {
            container.innerHTML = '';
        }

        if (this.communityPosts.length === 0) {
            container.innerHTML = `
                <div class="post-card">
                    <p>게시글이 없습니다.</p>
                    <p>첫 번째 게시글을 작성해보세요!</p>
                </div>
            `;
            return;
        }

        const startIndex = this.currentPage * this.postsPerPage;
        const endIndex = startIndex + this.postsPerPage;
        const postsToShow = this.communityPosts.slice(startIndex, endIndex);

        postsToShow.forEach(post => {
            const card = document.createElement('div');
            card.className = 'post-card';
            
            let healthDataPreview = '';
            if (post.heartrate > 0 || post.temperature > 0 || post.bloodpress > 0) {
                healthDataPreview = '<div class="health-data-preview">';
                if (post.heartrate > 0) {
                    healthDataPreview += `<span class="health-data-text">심박수: ${post.heartrate} bpm</span>`;
                }
                if (post.temperature > 0) {
                    healthDataPreview += `<span class="health-data-text">체온: ${post.temperature}°C</span>`;
                }
                if (post.bloodpress > 0) {
                    healthDataPreview += `<span class="health-data-text">혈압: ${post.bloodpress}</span>`;
                }
                healthDataPreview += '</div>';
            }

            card.innerHTML = `
                <div class="post-header">
                    <span class="post-author">${post.userNm}</span>
                    <span class="post-date">${new Date(post.regDate).toLocaleDateString('ko-KR', {
                        year: 'numeric',
                        month: 'short',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                    })}</span>
                </div>
                <div class="post-content">${post.content}</div>
                ${healthDataPreview}
            `;
            
            card.addEventListener('click', () => {
                this.showPostDetailModal(post);
            });
            
            container.appendChild(card);
        });

        this.setupInfiniteScroll();
    }

    setupInfiniteScroll() {
        const postsContainer = document.getElementById('posts-container');
        const loadingMore = document.getElementById('loading-more');
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && !this.isLoadingMore) {
                    this.loadMorePosts();
                }
            });
        }, {
            threshold: 0.1
        });

        if (postsContainer.lastElementChild) {
            observer.observe(postsContainer.lastElementChild);
        }
    }

    loadMorePosts() {
        const totalPosts = this.communityPosts.length;
        const currentDisplayed = (this.currentPage + 1) * this.postsPerPage;
        
        if (currentDisplayed >= totalPosts) {
            return;
        }

        this.isLoadingMore = true;
        const loadingMore = document.getElementById('loading-more');
        loadingMore.style.display = 'block';

        setTimeout(() => {
            this.currentPage++;
            this.updateCommunity();
            this.isLoadingMore = false;
            loadingMore.style.display = 'none';
        }, 500);
    }

    updateProfile() {
        if (!this.currentUser) return;

        document.getElementById('avatar-text').textContent = this.currentUser.userNm.charAt(0);
        document.getElementById('profile-name').textContent = this.currentUser.userNm;
        document.getElementById('profile-email').textContent = this.currentUser.email;
        document.getElementById('profile-role').textContent = this.currentUser.userRoleFk;
        document.getElementById('profile-userid').textContent = this.currentUser.userId;
        document.getElementById('profile-username').textContent = this.currentUser.userNm;
        document.getElementById('profile-useremail').textContent = this.currentUser.email;
        document.getElementById('profile-phone').textContent = this.currentUser.telNumEnc;
        document.getElementById('profile-birth').textContent = this.currentUser.birthEnc;
        document.getElementById('profile-gender').textContent = this.currentUser.gender === 'M' ? '남성' : '여성';
        document.getElementById('profile-bloodtype').textContent = `${this.currentUser.bloodType}형`;
        document.getElementById('profile-height').textContent = `${this.currentUser.height}cm`;
        document.getElementById('profile-weight').textContent = `${this.currentUser.weight}kg`;
    }

    showHealthModal() {
        document.getElementById('health-modal').classList.remove('hidden');
        document.getElementById('input-heartrate').focus();
    }

    hideHealthModal() {
        document.getElementById('health-modal').classList.add('hidden');
        this.clearHealthForm();
    }

    clearHealthForm() {
        document.getElementById('input-heartrate').value = '';
        document.getElementById('input-temperature').value = '';
        document.getElementById('input-spo2').value = '';
        document.getElementById('input-step').value = '';
        document.getElementById('input-bp-max').value = '';
        document.getElementById('input-bp-min').value = '';
    }

    submitHealthData() {
        const formData = {
            heartrate: parseInt(document.getElementById('input-heartrate').value) || 0,
            temperature: parseFloat(document.getElementById('input-temperature').value) || 0,
            spo2: parseInt(document.getElementById('input-spo2').value) || 0,
            step: parseInt(document.getElementById('input-step').value) || 0,
            bloodpressMax: parseInt(document.getElementById('input-bp-max').value) || 0,
            bloodpressMin: parseInt(document.getElementById('input-bp-min').value) || 0
        };

        const newHealthData = {
            userId: this.currentUser.userId,
            time: new Date().toISOString(),
            heartrate: formData.heartrate,
            temperature: formData.temperature,
            spo2: formData.spo2,
            step: formData.step,
            stress: 3,
            bloodpressMin: formData.bloodpressMin,
            bloodpressMax: formData.bloodpressMax,
            repiratory: 16,
            sleep: 7
        };

        this.healthData.unshift(newHealthData);
        this.saveToStorage();
        this.hideHealthModal();
        this.healthCurrentPage = 0;
        this.updateHealthInfo();
        this.updateDashboard();
        this.showToast('건강 데이터가 저장되었습니다.', 'success');
    }

    showWriteModal() {
        document.getElementById('write-modal').classList.remove('hidden');
        document.getElementById('post-content').focus();
    }

    hideWriteModal() {
        document.getElementById('write-modal').classList.add('hidden');
        this.clearWriteForm();
    }

    clearWriteForm() {
        document.getElementById('post-content').value = '';
        document.getElementById('post-heartrate').value = '';
        document.getElementById('post-temperature').value = '';
        document.getElementById('post-bloodpress').value = '';
        document.getElementById('post-age').value = '';
    }

    submitPost() {
        const content = document.getElementById('post-content').value.trim();
        
        if (!content) {
            this.showToast('내용을 입력해주세요.', 'error');
            return;
        }

        const newPost = {
            commuSeq: Date.now(),
            content: content,
            regDate: new Date().toISOString(),
            heartrate: 0,
            temperature: 0,
            bloodpress: 0,
            smoking: 0,
            drinking: 0,
            exercise: 0,
            age: 0,
            userId: this.currentUser.userId,
            userNm: this.currentUser.userNm,
            bodyAge: 0
        };

        this.communityPosts.unshift(newPost);
        this.saveToStorage();
        this.hideWriteModal();
        this.currentPage = 0;
        this.updateCommunity();
        this.showToast('게시글이 작성되었습니다.', 'success');
    }

    showChart(chartType) {
        const modal = document.getElementById('chart-modal');
        const title = document.getElementById('modal-title');
        const canvas = document.getElementById('health-chart');
        
        modal.classList.remove('hidden');
        
        if (chartType === 'health-data') {
            title.textContent = '실시간 건강 데이터 차트';
            this.drawHealthDataChart(canvas);
        } else if (chartType === 'health-score') {
            title.textContent = '건강 점수 차트';
            this.drawHealthScoreChart(canvas);
        }
    }

    hideChart() {
        document.getElementById('chart-modal').classList.add('hidden');
    }

    drawHealthDataChart(canvas) {
        const ctx = canvas.getContext('2d');
        const width = canvas.width;
        const height = canvas.height;
        
        ctx.clearRect(0, 0, width, height);
        
        const recentData = this.healthData.slice(0, 7).reverse();
        const labels = recentData.map((_, index) => `${index + 1}일 전`);
        
        const heartrateData = recentData.map(data => data.heartrate);
        const temperatureData = recentData.map(data => data.temperature);
        const spo2Data = recentData.map(data => data.spo2);
        
        const maxValue = Math.max(...heartrateData, ...temperatureData.map(t => t * 10), ...spo2Data);
        const minValue = Math.min(...heartrateData, ...temperatureData.map(t => t * 10), ...spo2Data);
        const range = maxValue - minValue;
        
        const padding = 40;
        const chartWidth = width - padding * 2;
        const chartHeight = height - padding * 2 - 60;
        const barWidth = chartWidth / (heartrateData.length * 3 + 1);
        
        const colors = ['#2196F3', '#4CAF50', '#FF9800'];
        const dataSets = [
            { data: heartrateData, label: '심박수 (bpm)', color: colors[0] },
            { data: temperatureData.map(t => t * 10), label: '체온 (°C)', color: colors[1] },
            { data: spo2Data, label: '산소포화도 (%)', color: colors[2] }
        ];
        
        dataSets.forEach((dataSet, setIndex) => {
            ctx.fillStyle = dataSet.color;
            
            dataSet.data.forEach((value, index) => {
                const barHeight = ((value - minValue) / range) * chartHeight;
                const x = padding + (index * 3 + setIndex) * barWidth;
                const y = padding + chartHeight - barHeight;
                
                ctx.fillRect(x, y, barWidth * 0.8, barHeight);
            });
        });
        
        ctx.fillStyle = '#333';
        ctx.font = 'bold 18px Arial';
        ctx.textAlign = 'center';
        ctx.fillText('최근 7일 건강 데이터', width / 2, 30);
        
        ctx.font = '12px Arial';
        ctx.textAlign = 'left';
        
        const legendY = height - 60;
        dataSets.forEach((dataSet, index) => {
            ctx.fillStyle = dataSet.color;
            ctx.fillRect(30, legendY + (index * 18) - 10, 15, 10);
            ctx.fillStyle = '#333';
            ctx.fillText(dataSet.label, 55, legendY + (index * 18));
        });
    }

    drawHealthScoreChart(canvas) {
        const ctx = canvas.getContext('2d');
        const width = canvas.width;
        const height = canvas.height;
        
        ctx.clearRect(0, 0, width, height);
        
        if (!this.healthScore) return;
        
        const scores = [
            { label: '수면', value: this.healthScore.userSleepScore, color: '#2196F3' },
            { label: '운동', value: this.healthScore.userExerciseScore, color: '#4CAF50' },
            { label: '스트레스', value: this.healthScore.userStressScore, color: '#FF9800' }
        ];
        
        const centerX = width / 2;
        const centerY = height / 2;
        const radius = Math.min(width, height) / 4;
        
        let currentAngle = 0;
        
        scores.forEach((score, index) => {
            const sliceAngle = (score.value / 100) * Math.PI * 2;
            
            ctx.beginPath();
            ctx.moveTo(centerX, centerY);
            ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle);
            ctx.closePath();
            ctx.fillStyle = score.color;
            ctx.fill();
            
            const labelAngle = currentAngle + sliceAngle / 2;
            const labelX = centerX + Math.cos(labelAngle) * (radius + 30);
            const labelY = centerY + Math.sin(labelAngle) * (radius + 30);
            
            ctx.fillStyle = '#333';
            ctx.font = 'bold 14px Arial';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(score.value, labelX, labelY);
            
            ctx.font = '12px Arial';
            ctx.fillText(score.label, labelX, labelY + 18);
            
            currentAngle += sliceAngle;
        });
        
        ctx.fillStyle = '#333';
        ctx.font = 'bold 20px Arial';
        ctx.textAlign = 'center';
        ctx.fillText('건강 점수', centerX, centerY - radius - 40);
        
        ctx.font = 'bold 24px Arial';
        ctx.fillText(this.healthScore.healthScore, centerX, centerY);
        ctx.font = '14px Arial';
        ctx.fillText('점', centerX, centerY + 25);
    }

    refreshData() {
        this.showToast('데이터를 새로고침합니다...', 'info');
        
        setTimeout(() => {
            this.updateDashboard();
            this.showToast('데이터가 업데이트되었습니다.', 'success');
        }, 1000);
    }

    showEditProfileModal() {
        document.getElementById('edit-name').value = this.currentUser.userNm;
        document.getElementById('edit-email').value = this.currentUser.email;
        document.getElementById('edit-phone').value = this.currentUser.telNumEnc;
        document.getElementById('edit-height').value = this.currentUser.height;
        document.getElementById('edit-weight').value = this.currentUser.weight;
        
        document.getElementById('edit-profile-modal').classList.remove('hidden');
        document.getElementById('edit-name').focus();
    }

    hideEditProfileModal() {
        document.getElementById('edit-profile-modal').classList.add('hidden');
        this.clearEditProfileForm();
    }

    clearEditProfileForm() {
        document.getElementById('edit-name').value = '';
        document.getElementById('edit-email').value = '';
        document.getElementById('edit-phone').value = '';
        document.getElementById('edit-height').value = '';
        document.getElementById('edit-weight').value = '';
    }

    saveProfileChanges() {
        const name = document.getElementById('edit-name').value;
        const email = document.getElementById('edit-email').value;
        const phone = document.getElementById('edit-phone').value;
        const height = parseInt(document.getElementById('edit-height').value);
        const weight = parseInt(document.getElementById('edit-weight').value);

        if (!name || !email || !phone) {
            this.showToast('필수 정보를 모두 입력해주세요.', 'error');
            return;
        }

        this.currentUser.userNm = name;
        this.currentUser.email = email;
        this.currentUser.telNumEnc = phone;
        this.currentUser.height = height;
        this.currentUser.weight = weight;

        this.saveToStorage();
        this.updateProfile();
        this.updateDashboard();
        
        this.hideEditProfileModal();
        this.showToast('정보가 업데이트되었습니다.', 'success');
    }

    showChangePasswordModal() {
        document.getElementById('change-password-modal').classList.remove('hidden');
        document.getElementById('current-password').focus();
    }

    hideChangePasswordModal() {
        document.getElementById('change-password-modal').classList.add('hidden');
        this.clearChangePasswordForm();
    }

    clearChangePasswordForm() {
        document.getElementById('current-password').value = '';
        document.getElementById('new-password').value = '';
        document.getElementById('confirm-password').value = '';
    }

    savePasswordChanges() {
        const currentPassword = document.getElementById('current-password').value;
        const newPassword = document.getElementById('new-password').value;
        const confirmPassword = document.getElementById('confirm-password').value;

        if (!currentPassword || !newPassword || !confirmPassword) {
            this.showToast('모든 필드를 입력해주세요.', 'error');
            return;
        }

        if (newPassword !== confirmPassword) {
            this.showToast('새 비밀번호가 일치하지 않습니다.', 'error');
            return;
        }

        if (newPassword.length < 6) {
            this.showToast('비밀번호는 6자 이상이어야 합니다.', 'error');
            return;
        }

        this.hideChangePasswordModal();
        this.showToast('비밀번호가 변경되었습니다.', 'success');
    }

    showPostDetailModal(post) {
        document.getElementById('detail-author-initial').textContent = post.userNm.charAt(0);
        document.getElementById('detail-author-name').textContent = post.userNm;
        document.getElementById('detail-post-date').textContent = new Date(post.regDate).toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        document.getElementById('detail-post-content').textContent = post.content;

        const healthDataContainer = document.getElementById('detail-health-data');
        healthDataContainer.innerHTML = '';

        if (post.heartrate > 0 || post.temperature > 0 || post.bloodpress > 0 || post.age > 0) {
            const healthDataDiv = document.createElement('div');
            healthDataDiv.className = 'health-data-detail';
            healthDataDiv.innerHTML = '<h4>건강 데이터</h4>';
            
            const healthDataList = document.createElement('div');
            healthDataList.className = 'health-data-list';
            
            if (post.heartrate > 0) {
                const heartrateItem = document.createElement('div');
                heartrateItem.className = 'health-data-item';
                heartrateItem.innerHTML = `<span class="health-label">심박수:</span> <span class="health-value">${post.heartrate} bpm</span>`;
                healthDataList.appendChild(heartrateItem);
            }
            
            if (post.temperature > 0) {
                const temperatureItem = document.createElement('div');
                temperatureItem.className = 'health-data-item';
                temperatureItem.innerHTML = `<span class="health-label">체온:</span> <span class="health-value">${post.temperature}°C</span>`;
                healthDataList.appendChild(temperatureItem);
            }
            
            if (post.bloodpress > 0) {
                const bloodpressItem = document.createElement('div');
                bloodpressItem.className = 'health-data-item';
                bloodpressItem.innerHTML = `<span class="health-label">혈압:</span> <span class="health-value">${post.bloodpress}</span>`;
                healthDataList.appendChild(bloodpressItem);
            }
            
            if (post.age > 0) {
                const ageItem = document.createElement('div');
                ageItem.className = 'health-data-item';
                ageItem.innerHTML = `<span class="health-label">나이:</span> <span class="health-value">${post.age}세</span>`;
                healthDataList.appendChild(ageItem);
            }
            
            healthDataDiv.appendChild(healthDataList);
            healthDataContainer.appendChild(healthDataDiv);
        }

        document.getElementById('post-detail-modal').classList.remove('hidden');
    }

    hidePostDetailModal() {
        document.getElementById('post-detail-modal').classList.add('hidden');
    }

    getHealthStatus(value, type) {
        switch (type) {
            case 'heartrate':
                if (value < 60) return { status: '낮음', class: 'health-status-low' };
                if (value > 100) return { status: '높음', class: 'health-status-high' };
                return { status: '정상', class: 'health-status-normal' };
            case 'temperature':
                if (value < 36.1) return { status: '낮음', class: 'health-status-low' };
                if (value > 37.2) return { status: '높음', class: 'health-status-high' };
                return { status: '정상', class: 'health-status-normal' };
            case 'spo2':
                if (value < 95) return { status: '낮음', class: 'health-status-high' };
                return { status: '정상', class: 'health-status-normal' };
            default:
                return { status: '정상', class: 'health-status-normal' };
        }
    }

    showButtonLoading(formId) {
        const form = document.getElementById(formId);
        const button = form.querySelector('.auth-button');
        const buttonText = button.querySelector('.button-text');
        const buttonLoading = button.querySelector('.button-loading');
        
        button.disabled = true;
        buttonText.classList.add('hidden');
        buttonLoading.classList.remove('hidden');
    }

    hideButtonLoading(formId) {
        const form = document.getElementById(formId);
        const button = form.querySelector('.auth-button');
        const buttonText = button.querySelector('.button-text');
        const buttonLoading = button.querySelector('.button-loading');
        
        button.disabled = false;
        buttonText.classList.remove('hidden');
        buttonLoading.classList.add('hidden');
    }

    showToast(message, type = 'info') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        
        container.appendChild(toast);
        
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 3000);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new HealthcareApp();
});

document.addEventListener('DOMContentLoaded', () => {
    document.documentElement.style.scrollBehavior = 'smooth';
    
    document.querySelectorAll('button').forEach(button => {
        button.addEventListener('click', function() {
            if (!this.disabled) {
                this.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    this.style.transform = 'scale(1)';
                }, 150);
            }
        });
    });
    
    document.querySelectorAll('input[required]').forEach(input => {
        input.addEventListener('blur', function() {
            if (!this.value.trim()) {
                this.style.borderColor = '#F44336';
            } else {
                this.style.borderColor = '#ddd';
            }
        });
    });
    
    setInterval(() => {
        if (window.healthcareApp && window.healthcareApp.isAuthenticated) {
            const heartrateElement = document.getElementById('heartrate-value');
            if (heartrateElement && !heartrateElement.closest('.hidden')) {
                const currentValue = parseInt(heartrateElement.textContent);
                const variation = Math.floor(Math.random() * 6) - 3;
                const newValue = Math.max(60, Math.min(100, currentValue + variation));
                heartrateElement.textContent = `${newValue} bpm`;
            }
        }
    }, 5000);
});

window.healthcareApp = null;
document.addEventListener('DOMContentLoaded', () => {
    window.healthcareApp = new HealthcareApp();
});
