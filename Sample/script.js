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
        this.checkupData = [];
        this.checkupCurrentPage = 0;
        this.checkupPostsPerPage = 5;
        this.isLoadingMoreCheckup = false;
        this.currentTab = 'realtime';
        this.dietPlan = null;
        this.mealRecords = [];
        this.nutritionGoals = null;
        this.foodDatabase = this.initializeFoodDatabase();
        this.dietCurrentTab = 'diet-plan';
        this.exerciseProgram = null;
        this.exerciseRecords = [];
        this.exerciseDatabase = this.initializeExerciseDatabase();
        this.exerciseCurrentTab = 'exercise-program';
        this.contentDatabase = this.initializeContentDatabase();
        this.serviceDatabase = this.initializeServiceDatabase();
        this.contentViewHistory = [];
        this.recommendCurrentTab = 'content-recommend';
        this.doctorDatabase = this.initializeDoctorDatabase();
        this.consultations = [];
        this.consultationCurrentTab = 'doctor-list';
        this.serviceReservations = [];
        this.isDropdownOpening = false;
        this.settings = this.loadSettings();
        this.notifications = [];
        this.isSubmittingConsultation = false;
        
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadDummyData();
        this.loadFromStorage();
        this.loadNotifications();
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
                if (screen) {
                this.navigateToScreen(screen);
                }
            });
        });

        document.getElementById('refresh-btn').addEventListener('click', () => {
            this.refreshData();
        });

        document.getElementById('notification-btn').addEventListener('click', () => {
            this.navigateToScreen('notification');
        });

        document.getElementById('mark-all-read-btn-header').addEventListener('click', () => {
            this.markAllNotificationsAsRead();
        });

        const notificationMenuItem = document.getElementById('notification-menu-item');
        if (notificationMenuItem) {
            notificationMenuItem.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                this.hideNavDropdown();
                setTimeout(() => {
                    this.navigateToScreen('notification');
                }, 100);
            });
        }


        document.querySelectorAll('#notification-filters .filter-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                document.querySelectorAll('#notification-filters .filter-btn').forEach(b => b.classList.remove('active'));
                e.target.classList.add('active');
                this.updateNotificationScreen();
            });
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

        document.querySelectorAll('.tab-button').forEach(button => {
            button.addEventListener('click', (e) => {
                const tab = e.currentTarget.dataset.tab;
                this.switchTab(tab);
            });
        });

        document.getElementById('show-checkup-upload').addEventListener('click', () => {
            this.showCheckupUploadModal();
        });

        document.getElementById('close-checkup-modal').addEventListener('click', () => {
            this.hideCheckupUploadModal();
        });

        document.getElementById('cancel-checkup').addEventListener('click', () => {
            this.hideCheckupUploadModal();
        });

        document.getElementById('checkup-upload-modal').addEventListener('click', (e) => {
            if (e.target.id === 'checkup-upload-modal') {
                this.hideCheckupUploadModal();
            }
        });

        document.getElementById('add-checkup-item').addEventListener('click', () => {
            this.addCheckupItem();
        });

        document.getElementById('submit-checkup').addEventListener('click', () => {
            this.submitCheckup();
        });

        document.getElementById('close-checkup-detail-modal').addEventListener('click', () => {
            this.hideCheckupDetailModal();
        });

        document.getElementById('checkup-detail-modal').addEventListener('click', (e) => {
            if (e.target.id === 'checkup-detail-modal') {
                this.hideCheckupDetailModal();
            }
        });

        document.getElementById('view-checkup-trend').addEventListener('click', () => {
            this.showTrendModal();
        });

        document.getElementById('close-trend-modal').addEventListener('click', () => {
            this.hideTrendModal();
        });

        document.getElementById('checkup-trend-modal').addEventListener('click', (e) => {
            if (e.target.id === 'checkup-trend-modal') {
                this.hideTrendModal();
            }
        });

        document.getElementById('trend-item-select').addEventListener('change', () => {
            this.updateTrendChart();
        });

        document.getElementById('generate-diet-plan').addEventListener('click', () => {
            this.showDietPlanModal();
        });

        document.getElementById('add-meal-record').addEventListener('click', () => {
            this.showMealRecordModal();
        });

        document.getElementById('close-diet-plan-modal').addEventListener('click', () => {
            this.hideDietPlanModal();
        });

        document.getElementById('cancel-diet-plan').addEventListener('click', () => {
            this.hideDietPlanModal();
        });

        document.getElementById('diet-plan-modal').addEventListener('click', (e) => {
            if (e.target.id === 'diet-plan-modal') {
                this.hideDietPlanModal();
            }
        });

        document.getElementById('generate-diet-plan-btn').addEventListener('click', () => {
            this.generateDietPlan();
        });

        document.getElementById('close-meal-record-modal').addEventListener('click', () => {
            this.hideMealRecordModal();
        });

        document.getElementById('cancel-meal-record').addEventListener('click', () => {
            this.hideMealRecordModal();
        });

        document.getElementById('meal-record-modal').addEventListener('click', (e) => {
            if (e.target.id === 'meal-record-modal') {
                this.hideMealRecordModal();
            }
        });

        document.getElementById('add-food-item').addEventListener('click', () => {
            this.addFoodItem();
        });

        document.getElementById('submit-meal-record').addEventListener('click', () => {
            this.submitMealRecord();
        });

        document.querySelectorAll('[data-tab]').forEach(button => {
            if (button.closest('#diet-screen')) {
                button.addEventListener('click', (e) => {
                    const tab = e.currentTarget.dataset.tab;
                    this.switchDietTab(tab);
                });
            }
            if (button.closest('#exercise-screen')) {
                button.addEventListener('click', (e) => {
                    const tab = e.currentTarget.dataset.tab;
                    this.switchExerciseTab(tab);
                });
            }
        });

        document.getElementById('generate-exercise-program').addEventListener('click', () => {
            this.showExerciseProgramModal();
        });

        document.getElementById('add-exercise-record').addEventListener('click', () => {
            this.showExerciseRecordModal();
        });

        document.getElementById('close-exercise-program-modal').addEventListener('click', () => {
            this.hideExerciseProgramModal();
        });

        document.getElementById('cancel-exercise-program').addEventListener('click', () => {
            this.hideExerciseProgramModal();
        });

        document.getElementById('exercise-program-modal').addEventListener('click', (e) => {
            if (e.target.id === 'exercise-program-modal') {
                this.hideExerciseProgramModal();
            }
        });

        document.getElementById('generate-exercise-program-btn').addEventListener('click', () => {
            this.generateExerciseProgram();
        });

        document.getElementById('close-exercise-record-modal').addEventListener('click', () => {
            this.hideExerciseRecordModal();
        });

        document.getElementById('cancel-exercise-record').addEventListener('click', () => {
            this.hideExerciseRecordModal();
        });

        document.getElementById('exercise-record-modal').addEventListener('click', (e) => {
            if (e.target.id === 'exercise-record-modal') {
                this.hideExerciseRecordModal();
            }
        });

        document.getElementById('submit-exercise-record').addEventListener('click', () => {
            this.submitExerciseRecord();
        });

        document.getElementById('close-exercise-guide-modal').addEventListener('click', () => {
            this.hideExerciseGuideModal();
        });

        document.getElementById('exercise-guide-modal').addEventListener('click', (e) => {
            if (e.target.id === 'exercise-guide-modal') {
                this.hideExerciseGuideModal();
            }
        });

        document.getElementById('refresh-recommendations').addEventListener('click', () => {
            this.updateRecommendScreen();
        });

        document.querySelectorAll('[data-tab]').forEach(button => {
            if (button.closest('#recommend-screen')) {
                button.addEventListener('click', (e) => {
                    const tab = e.currentTarget.dataset.tab;
                    this.switchRecommendTab(tab);
                });
            }
        });

        document.getElementById('close-content-detail-modal').addEventListener('click', () => {
            this.hideContentDetailModal();
        });

        document.getElementById('content-detail-modal').addEventListener('click', (e) => {
            if (e.target.id === 'content-detail-modal') {
                this.hideContentDetailModal();
            }
        });

        document.getElementById('close-service-detail-modal').addEventListener('click', () => {
            this.hideServiceDetailModal();
        });

        document.getElementById('service-detail-modal').addEventListener('click', (e) => {
            if (e.target.id === 'service-detail-modal') {
                this.hideServiceDetailModal();
            }
        });

        const closeServiceReservationModal = document.getElementById('close-service-reservation-modal');
        if (closeServiceReservationModal) {
            closeServiceReservationModal.addEventListener('click', () => {
                this.hideServiceReservationModal();
            });
        }

        const cancelServiceReservation = document.getElementById('cancel-service-reservation');
        if (cancelServiceReservation) {
            cancelServiceReservation.addEventListener('click', () => {
                this.hideServiceReservationModal();
            });
        }

        const serviceReservationModal = document.getElementById('service-reservation-modal');
        if (serviceReservationModal) {
            serviceReservationModal.addEventListener('click', (e) => {
                if (e.target.id === 'service-reservation-modal') {
                    this.hideServiceReservationModal();
                }
            });
        }

        const serviceReservationForm = document.getElementById('service-reservation-form');
        if (serviceReservationForm) {
            serviceReservationForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.submitServiceReservation();
            });
        }

        document.getElementById('request-consultation-btn').addEventListener('click', () => {
            this.showConsultationRequestModal();
        });

        document.getElementById('close-consultation-request-modal').addEventListener('click', () => {
            this.hideConsultationRequestModal();
        });

        document.getElementById('cancel-consultation-request').addEventListener('click', () => {
            this.hideConsultationRequestModal();
        });

        document.getElementById('consultation-request-modal').addEventListener('click', (e) => {
            if (e.target.id === 'consultation-request-modal') {
                this.hideConsultationRequestModal();
            }
        });

        document.getElementById('consultation-request-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.submitConsultationRequest();
        });

        document.getElementById('close-consultation-detail-modal').addEventListener('click', () => {
            this.hideConsultationDetailModal();
        });

        document.getElementById('consultation-detail-modal').addEventListener('click', (e) => {
            if (e.target.id === 'consultation-detail-modal') {
                this.hideConsultationDetailModal();
            }
        });

        document.querySelectorAll('[data-tab]').forEach(button => {
            if (button.closest('#consultation-screen')) {
                button.addEventListener('click', (e) => {
                    const tab = e.currentTarget.dataset.tab;
                    this.switchConsultationTab(tab);
                });
            }
        });

        const navMoreBtn = document.getElementById('nav-more-btn');
        if (navMoreBtn) {
            navMoreBtn.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                e.stopImmediatePropagation();
                this.toggleNavDropdown();
            }, true);
        }


        let clickTimeout = null;
        document.addEventListener('click', (e) => {
            const moreBtn = document.getElementById('nav-more-btn');
            const dropdown = document.querySelector('.nav-dropdown');
            const menu = document.getElementById('nav-dropdown-menu');


            if (moreBtn && (moreBtn === e.target || moreBtn.contains(e.target))) {
                if (clickTimeout) {
                    clearTimeout(clickTimeout);
                    clickTimeout = null;
                }
                return;
            }


            if (menu && menu.contains(e.target)) {
                if (clickTimeout) {
                    clearTimeout(clickTimeout);
                    clickTimeout = null;
                }
                return;
            }


            if (this.isDropdownOpening) {
                if (clickTimeout) {
                    clearTimeout(clickTimeout);
                    clickTimeout = null;
                }
                return;
            }


            if (dropdown && menu && !menu.classList.contains('hidden')) {
                if (clickTimeout) {
                    clearTimeout(clickTimeout);
                }
                clickTimeout = setTimeout(() => {

                    if (menu && !menu.classList.contains('hidden')) {
                        this.hideNavDropdown();
                    }
                    clickTimeout = null;
                }, 150);
            }
        });


        const navDropdown = document.querySelector('.nav-dropdown');
        if (navDropdown) {
            navDropdown.addEventListener('click', (e) => {
                const dropdownItem = e.target.closest('.dropdown-item');
                if (dropdownItem) {
                    e.preventDefault();
                    e.stopPropagation();
                    const screen = dropdownItem.dataset.screen;
                    if (screen) {

                        this.hideNavDropdown();

                        setTimeout(() => {
                            this.navigateToScreen(screen);
                        }, 100);
                    }
                }
            });
        }

    }

    toggleNavDropdown() {
        const menu = document.getElementById('nav-dropdown-menu');
        const moreBtn = document.getElementById('nav-more-btn');

        if (!menu || !moreBtn) {
            return;
        }

        const isHidden = menu.classList.contains('hidden');

        if (isHidden) {
            this.isDropdownOpening = true;

            const rect = moreBtn.getBoundingClientRect();
            const leftPosition = rect.left + rect.width / 2;

            menu.style.setProperty('position', 'fixed', 'important');
            menu.style.setProperty('bottom', '70px', 'important');
            menu.style.setProperty('left', `${leftPosition}px`, 'important');
            menu.style.setProperty('transform', 'translateX(-50%)', 'important');
            menu.style.setProperty('z-index', '10000', 'important');
            menu.style.setProperty('background', 'white', 'important');
            menu.style.setProperty('border-radius', '12px', 'important');
            menu.style.setProperty('box-shadow', '0 -4px 12px rgba(0, 0, 0, 0.15)', 'important');
            menu.style.setProperty('min-width', '200px', 'important');
            menu.style.setProperty('padding', '0.5rem 0', 'important');
            menu.style.setProperty('flex-direction', 'column', 'important');
            menu.style.setProperty('max-height', '70vh', 'important');
            menu.style.setProperty('overflow-y', 'auto', 'important');

            menu.classList.remove('hidden');
            menu.classList.add('show');
            menu.style.setProperty('display', 'flex', 'important');
            menu.style.setProperty('visibility', 'visible', 'important');
            menu.style.setProperty('opacity', '1', 'important');
            menu.style.setProperty('pointer-events', 'auto', 'important');

            requestAnimationFrame(() => {
                setTimeout(() => {
                    this.isDropdownOpening = false;
                }, 50);
            });
        } else {

            menu.classList.add('hidden');
            menu.classList.remove('show');
            menu.style.setProperty('display', 'none', 'important');
            menu.style.setProperty('visibility', 'hidden', 'important');
            menu.style.setProperty('opacity', '0', 'important');
            menu.style.setProperty('pointer-events', 'none', 'important');
        }
    }

    hideNavDropdown() {
        const menu = document.getElementById('nav-dropdown-menu');
        if (menu) {
            menu.classList.add('hidden');
            menu.classList.remove('show');
            menu.style.setProperty('display', 'none', 'important');
            menu.style.setProperty('visibility', 'hidden', 'important');
            menu.style.setProperty('opacity', '0', 'important');
            menu.style.setProperty('pointer-events', 'none', 'important');
        }
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

        if (this.checkupData.length === 0) {
            this.checkupData = [];
            const sampleCheckups = [
                {
                    checkupSeq: 1,
                    userId: 'user123',
                    checkupDate: '2023-01-15',
                    checkupType: 'GENERAL',
                    hospitalName: '서울대학교병원',
                    items: [
                        { itemCode: 'BLOOD_PRESSURE', itemName: '혈압', itemValue: '120/80', unit: 'mmHg', referenceRange: '120/80 이하', status: 'NORMAL' },
                        { itemCode: 'CHOLESTEROL', itemName: '총콜레스테롤', itemValue: '180', unit: 'mg/dL', referenceRange: '200 이하', status: 'NORMAL' },
                        { itemCode: 'GLUCOSE', itemName: '공복혈당', itemValue: '95', unit: 'mg/dL', referenceRange: '100 이하', status: 'NORMAL' }
                    ]
                },
                {
                    checkupSeq: 2,
                    userId: 'user123',
                    checkupDate: '2023-07-20',
                    checkupType: 'GENERAL',
                    hospitalName: '서울대학교병원',
                    items: [
                        { itemCode: 'BLOOD_PRESSURE', itemName: '혈압', itemValue: '125/82', unit: 'mmHg', referenceRange: '120/80 이하', status: 'NORMAL' },
                        { itemCode: 'CHOLESTEROL', itemName: '총콜레스테롤', itemValue: '195', unit: 'mg/dL', referenceRange: '200 이하', status: 'NORMAL' },
                        { itemCode: 'GLUCOSE', itemName: '공복혈당', itemValue: '98', unit: 'mg/dL', referenceRange: '100 이하', status: 'NORMAL' }
                    ]
                },
                {
                    checkupSeq: 3,
                    userId: 'user123',
                    checkupDate: '2024-01-15',
                    checkupType: 'GENERAL',
                    hospitalName: '서울대학교병원',
                    items: [
                        { itemCode: 'BLOOD_PRESSURE', itemName: '혈압', itemValue: '130/85', unit: 'mmHg', referenceRange: '120/80 이하', status: 'ABNORMAL' },
                        { itemCode: 'CHOLESTEROL', itemName: '총콜레스테롤', itemValue: '220', unit: 'mg/dL', referenceRange: '200 이하', status: 'ABNORMAL' },
                        { itemCode: 'GLUCOSE', itemName: '공복혈당', itemValue: '105', unit: 'mg/dL', referenceRange: '100 이하', status: 'ABNORMAL' }
                    ]
                }
            ];
            this.checkupData = sampleCheckups;
            this.checkupData.sort((a, b) => new Date(b.checkupDate) - new Date(a.checkupDate));
        }
    }

    saveToStorage() {
        localStorage.setItem('healthcare_healthData', JSON.stringify(this.healthData));
        localStorage.setItem('healthcare_communityPosts', JSON.stringify(this.communityPosts));
        localStorage.setItem('healthcare_healthScore', JSON.stringify(this.healthScore));
        localStorage.setItem('healthcare_currentUser', JSON.stringify(this.currentUser));
        localStorage.setItem('healthcare_checkupData', JSON.stringify(this.checkupData));
        localStorage.setItem('healthcare_dietPlan', JSON.stringify(this.dietPlan));
        localStorage.setItem('healthcare_mealRecords', JSON.stringify(this.mealRecords));
        localStorage.setItem('healthcare_nutritionGoals', JSON.stringify(this.nutritionGoals));
        localStorage.setItem('healthcare_exerciseProgram', JSON.stringify(this.exerciseProgram));
        localStorage.setItem('healthcare_exerciseRecords', JSON.stringify(this.exerciseRecords));
        localStorage.setItem('healthcare_contentViewHistory', JSON.stringify(this.contentViewHistory));
        localStorage.setItem('healthcare_consultations', JSON.stringify(this.consultations));
        localStorage.setItem('healthcare_serviceReservations', JSON.stringify(this.serviceReservations));
        this.saveNotifications();
    }

    loadFromStorage() {
        const savedHealthData = localStorage.getItem('healthcare_healthData');
        const savedCommunityPosts = localStorage.getItem('healthcare_communityPosts');
        const savedHealthScore = localStorage.getItem('healthcare_healthScore');
        const savedCurrentUser = localStorage.getItem('healthcare_currentUser');
        const savedCheckupData = localStorage.getItem('healthcare_checkupData');
        const savedDietPlan = localStorage.getItem('healthcare_dietPlan');
        const savedMealRecords = localStorage.getItem('healthcare_mealRecords');
        const savedNutritionGoals = localStorage.getItem('healthcare_nutritionGoals');
        const savedExerciseProgram = localStorage.getItem('healthcare_exerciseProgram');
        const savedExerciseRecords = localStorage.getItem('healthcare_exerciseRecords');
        const savedContentViewHistory = localStorage.getItem('healthcare_contentViewHistory');
        const savedConsultations = localStorage.getItem('healthcare_consultations');
        const savedServiceReservations = localStorage.getItem('healthcare_serviceReservations');

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
        if (savedCheckupData) {
            this.checkupData = JSON.parse(savedCheckupData);
        }
        if (savedDietPlan) {
            this.dietPlan = JSON.parse(savedDietPlan);
        }
        if (savedMealRecords) {
            this.mealRecords = JSON.parse(savedMealRecords);
        }
        if (savedNutritionGoals) {
            this.nutritionGoals = JSON.parse(savedNutritionGoals);
        }
        if (savedExerciseProgram) {
            this.exerciseProgram = JSON.parse(savedExerciseProgram);
        }
        if (savedExerciseRecords) {
            this.exerciseRecords = JSON.parse(savedExerciseRecords);
        }
        if (savedContentViewHistory) {
            this.contentViewHistory = JSON.parse(savedContentViewHistory);
        }
        if (savedConsultations) {
            this.consultations = JSON.parse(savedConsultations);
        }
        if (savedServiceReservations) {
            this.serviceReservations = JSON.parse(savedServiceReservations);
        }


        if (this.settings && this.settings.darkMode) {
            this.applyDarkMode(true);
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
        if (!screenName) {
            console.error('screenName이 제공되지 않았습니다.');
            return;
        }

        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelectorAll('.dropdown-item').forEach(item => {
            item.classList.remove('active');
        });

        const navItem = document.querySelector(`.nav-item[data-screen="${screenName}"]`);
        const dropdownItem = document.querySelector(`.dropdown-item[data-screen="${screenName}"]`);

        if (navItem) {
            navItem.classList.add('active');
        }
        if (dropdownItem) {
            dropdownItem.classList.add('active');
        }

        document.querySelectorAll('.main-screen').forEach(screen => {
            screen.classList.add('hidden');
        });

        const targetScreen = document.getElementById(`${screenName}-screen`);
        if (!targetScreen) {
            console.error(`화면을 찾을 수 없습니다: ${screenName}-screen`);
            return;
        }

        targetScreen.classList.remove('hidden');

        this.currentScreen = screenName;

        switch (screenName) {
            case 'dashboard':
                this.updateDashboard();
                break;
            case 'healthinfo':
                this.updateHealthInfo();
                this.updateCheckupInfo();
                break;
            case 'diet':
                this.updateDietScreen();
                break;
            case 'exercise':
                this.updateExerciseScreen();
                break;
            case 'recommend':
                this.updateRecommendScreen();
                break;
            case 'consultation':
                this.updateConsultationScreen();
                break;
            case 'community':
                this.updateCommunity();
                break;
            case 'settings':
                this.updateSettingsScreen();
                break;
            case 'notification':
                this.updateNotificationScreen();
                break;
            case 'profile':
                this.updateProfile();
                break;
        }
    }

    switchDietTab(tabName) {
        this.dietCurrentTab = tabName;
        document.querySelectorAll('#diet-screen .tab-button').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelectorAll('#diet-screen .tab-content').forEach(content => {
            content.classList.remove('active');
        });
        document.querySelector(`#diet-screen [data-tab="${tabName}"]`).classList.add('active');
        document.getElementById(`tab-${tabName}`).classList.add('active');

        if (tabName === 'nutrition-analysis') {
            this.updateNutritionAnalysis();
        } else if (tabName === 'meal-records') {
            this.updateMealRecords();
        } else if (tabName === 'diet-plan') {
            this.updateDietPlan();
        }
    }

    switchTab(tabName) {
        this.currentTab = tabName;
        document.querySelectorAll('.tab-button').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.remove('active');
        });
        document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
        document.getElementById(`tab-${tabName}`).classList.add('active');

        if (tabName === 'checkup') {
            this.updateCheckupInfo();
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

        this.updateCheckupSummary();
        this.generateAIAdvice();
    }

    updateCheckupSummary() {
        const container = document.getElementById('checkup-summary-content');

        if (this.checkupData.length === 0) {
            container.innerHTML = '<p class="no-checkup-data">건강검진 결과가 없습니다.</p>';
            return;
        }

        const latestCheckup = this.checkupData[0];
        const abnormalCount = latestCheckup.items.filter(item => item.status === 'ABNORMAL').length;
        const riskLevel = this.calculateRiskLevel(latestCheckup);
        const daysSinceCheckup = Math.floor((new Date() - new Date(latestCheckup.checkupDate)) / (1000 * 60 * 60 * 24));

        container.innerHTML = `
            <div class="checkup-summary-item">
                <div class="summary-header">
                    <span class="summary-label">최근 검진일</span>
                    <span class="summary-value">${latestCheckup.checkupDate}</span>
                </div>
                <div class="summary-header">
                    <span class="summary-label">검진 유형</span>
                    <span class="summary-value">${this.getCheckupTypeName(latestCheckup.checkupType)}</span>
                </div>
                <div class="summary-header">
                    <span class="summary-label">병원</span>
                    <span class="summary-value">${latestCheckup.hospitalName}</span>
                </div>
                <div class="summary-header">
                    <span class="summary-label">이상 지표</span>
                    <span class="summary-value ${abnormalCount > 0 ? 'abnormal' : 'normal'}">
                        ${abnormalCount}개
                    </span>
                </div>
                <div class="summary-header">
                    <span class="summary-label">위험도</span>
                    <span class="summary-risk ${riskLevel.class}">
                        <i class="fas ${riskLevel.icon}"></i>
                        ${riskLevel.text}
                    </span>
                </div>
                ${daysSinceCheckup > 365 ? `
                    <div class="checkup-alert-summary">
                        <i class="fas fa-exclamation-triangle"></i>
                        <span>마지막 검진 후 ${Math.floor(daysSinceCheckup / 365)}년이 지났습니다. 정기 검진을 권장합니다.</span>
                    </div>
                ` : ''}
        `;
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

    updateCheckupInfo() {
        const container = document.getElementById('checkup-cards');

        if (this.checkupCurrentPage === 0) {
            container.innerHTML = '';
        }

        if (this.checkupData.length === 0) {
            container.innerHTML = `
                <div class="checkup-card">
                    <p>건강검진 결과가 없습니다.</p>
                    <p>검진 결과를 업로드해보세요!</p>
                </div>
            `;
            return;
        }

        const startIndex = this.checkupCurrentPage * this.checkupPostsPerPage;
        const endIndex = startIndex + this.checkupPostsPerPage;
        const dataToShow = this.checkupData.slice(startIndex, endIndex);

        dataToShow.forEach((checkup) => {
            const card = document.createElement('div');
            card.className = 'checkup-card';

            const abnormalCount = checkup.items.filter(item => item.status === 'ABNORMAL').length;
            const riskLevel = this.calculateRiskLevel(checkup);

            card.innerHTML = `
                <div class="checkup-header">
                    <div class="checkup-title">
                        <h4>${this.getCheckupTypeName(checkup.checkupType)}</h4>
                        <span class="checkup-date">${checkup.checkupDate}</span>
                    </div>
                    <div class="checkup-risk ${riskLevel.class}">
                        <i class="fas ${riskLevel.icon}"></i>
                        <span>${riskLevel.text}</span>
                    </div>
                </div>
                <div class="checkup-info">
                    <span class="checkup-hospital"><i class="fas fa-hospital"></i> ${checkup.hospitalName}</span>
                    <span class="checkup-items-count">검진 항목: ${checkup.items.length}개</span>
                </div>
                ${abnormalCount > 0 ? `
                    <div class="checkup-alert">
                        <i class="fas fa-exclamation-triangle"></i>
                        <span>이상 지표 ${abnormalCount}개 발견</span>
                    </div>
                ` : ''}
                <div class="checkup-preview">
                    ${checkup.items.slice(0, 3).map(item => `
                        <div class="checkup-item-preview ${item.status === 'ABNORMAL' ? 'abnormal' : ''}">
                            <span class="item-name">${item.itemName}</span>
                            <span class="item-value">${item.itemValue} ${item.unit}</span>
                            <span class="item-status ${item.status === 'ABNORMAL' ? 'abnormal' : 'normal'}">${item.status === 'ABNORMAL' ? '이상' : '정상'}</span>
                        </div>
                    `).join('')}
                    ${checkup.items.length > 3 ? `<div class="more-items">+${checkup.items.length - 3}개 더 보기</div>` : ''}
                </div>
            `;

            card.addEventListener('click', () => {
                this.showCheckupDetailModal(checkup);
            });

            container.appendChild(card);
        });

        this.setupCheckupInfiniteScroll();
        this.detectAbnormalIndicators();
    }

    setupCheckupInfiniteScroll() {
        const checkupContainer = document.getElementById('checkup-cards');
        const loadingMore = document.getElementById('loading-more-checkup');

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && !this.isLoadingMoreCheckup) {
                    this.loadMoreCheckupData();
                }
            });
        }, {
            threshold: 0.1
        });

        if (checkupContainer.lastElementChild) {
            observer.observe(checkupContainer.lastElementChild);
        }
    }

    loadMoreCheckupData() {
        const totalData = this.checkupData.length;
        const currentDisplayed = (this.checkupCurrentPage + 1) * this.checkupPostsPerPage;

        if (currentDisplayed >= totalData) {
            return;
        }

        this.isLoadingMoreCheckup = true;
        const loadingMore = document.getElementById('loading-more-checkup');
        loadingMore.style.display = 'block';

        setTimeout(() => {
            this.checkupCurrentPage++;
            this.updateCheckupInfo();
            this.isLoadingMoreCheckup = false;
            loadingMore.style.display = 'none';
        }, 500);
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

        this.updateProfileReservations();
    }

    updateProfileReservations() {
        const container = document.getElementById('profile-reservations-list');
        if (!container) return;

        if (this.serviceReservations.length === 0) {
            container.innerHTML = `
                <div class="empty-history">
                    <i class="fas fa-calendar-check"></i>
                    <p>예약 이력이 없습니다.</p>
                </div>
            `;
            return;
        }

        const sortedReservations = [...this.serviceReservations].sort((a, b) => 
            new Date(b.datetime) - new Date(a.datetime)
        );

        container.innerHTML = sortedReservations.slice(0, 5).map(reservation => `
            <div class="history-item">
                <div class="history-header">
                    <div class="history-title">
                        <i class="fas fa-hospital"></i>
                        <span>${reservation.serviceName}</span>
                    </div>
                    <span class="status-badge status-${reservation.status.toLowerCase()}">
                        ${reservation.statusName}
                    </span>
                </div>
                <div class="history-details">
                    <span><i class="fas fa-stethoscope"></i> ${reservation.departmentName}</span>
                    <span><i class="fas fa-calendar-alt"></i> ${this.formatDateTime(reservation.datetime)}</span>
                </div>
                <div class="history-type">
                    <span class="type-tag">${reservation.typeName}</span>
                    ${reservation.symptoms ? `<span class="symptoms-preview">${reservation.symptoms.substring(0, 30)}${reservation.symptoms.length > 30 ? '...' : ''}</span>` : ''}
                </div>
            </div>
        `).join('');

        if (this.serviceReservations.length > 5) {
            const moreBtn = document.createElement('div');
            moreBtn.className = 'view-more-btn';
            moreBtn.innerHTML = `
                <button>
                    전체 보기 (${this.serviceReservations.length}건)
                </button>
            `;
            container.appendChild(moreBtn);
        }
    }

    formatDateTime(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
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

    showCheckupUploadModal() {
        document.getElementById('checkup-upload-modal').classList.remove('hidden');
        document.getElementById('checkup-items-container').innerHTML = '';
        document.getElementById('checkup-date').value = new Date().toISOString().split('T')[0];
        document.getElementById('checkup-type').value = '';
        document.getElementById('hospital-name').value = '';
        this.addCheckupItem();
    }

    hideCheckupUploadModal() {
        document.getElementById('checkup-upload-modal').classList.add('hidden');
    }

    addCheckupItem() {
        const container = document.getElementById('checkup-items-container');
        const itemIndex = container.children.length;
        const itemDiv = document.createElement('div');
        itemDiv.className = 'checkup-item-input';
        itemDiv.innerHTML = `
            <div class="input-row">
                <div class="input-group">
                    <label>항목명 *</label>
                    <input type="text" class="item-name" placeholder="예: 총콜레스테롤" required />
                </div>
                <div class="input-group">
                    <label>측정값 *</label>
                    <input type="text" class="item-value" placeholder="예: 220" required />
                </div>
            </div>
            <div class="input-row">
                <div class="input-group">
                    <label>단위</label>
                    <input type="text" class="item-unit" placeholder="예: mg/dL" />
                </div>
                <div class="input-group">
                    <label>기준 범위</label>
                    <input type="text" class="item-reference" placeholder="예: 200 이하" />
                </div>
            </div>
            <button type="button" class="remove-item-button" onclick="this.parentElement.remove()">
                <i class="fas fa-times"></i> 제거
            </button>
        `;
        container.appendChild(itemDiv);
    }

    submitCheckup() {
        const checkupDate = document.getElementById('checkup-date').value;
        const checkupType = document.getElementById('checkup-type').value;
        const hospitalName = document.getElementById('hospital-name').value;

        if (!checkupDate || !checkupType || !hospitalName) {
            this.showToast('필수 정보를 모두 입력해주세요.', 'error');
            return;
        }

        const itemInputs = document.querySelectorAll('.checkup-item-input');
        const items = [];

        itemInputs.forEach(input => {
            const itemName = input.querySelector('.item-name').value;
            const itemValue = input.querySelector('.item-value').value;
            const itemUnit = input.querySelector('.item-unit').value || '';
            const itemReference = input.querySelector('.item-reference').value || '';

            if (!itemName || !itemValue) {
                this.showToast('모든 검진 항목의 항목명과 측정값을 입력해주세요.', 'error');
                return;
            }

            const itemCode = this.getItemCode(itemName);
            const status = this.determineItemStatus(itemName, itemValue, itemReference);

            items.push({
                itemCode: itemCode,
                itemName: itemName,
                itemValue: itemValue,
                unit: itemUnit,
                referenceRange: itemReference,
                status: status
            });
        });

        if (items.length === 0) {
            this.showToast('최소 1개 이상의 검진 항목을 입력해주세요.', 'error');
            return;
        }

        const newCheckup = {
            checkupSeq: Date.now(),
            userId: this.currentUser.userId,
            checkupDate: checkupDate,
            checkupType: checkupType,
            hospitalName: hospitalName,
            items: items
        };

        this.checkupData.unshift(newCheckup);
        this.checkupData.sort((a, b) => new Date(b.checkupDate) - new Date(a.checkupDate));
        this.saveToStorage();
        this.hideCheckupUploadModal();
        this.checkupCurrentPage = 0;
        this.updateCheckupInfo();
        this.detectAbnormalIndicators();
        this.showToast('건강검진 결과가 저장되었습니다.', 'success');
    }

    getItemCode(itemName) {
        const codeMap = {
            '혈압': 'BLOOD_PRESSURE',
            '총콜레스테롤': 'CHOLESTEROL',
            '공복혈당': 'GLUCOSE',
            '헤모글로빈': 'HEMOGLOBIN',
            '간기능': 'LIVER_FUNCTION',
            '신장기능': 'KIDNEY_FUNCTION'
        };
        return codeMap[itemName] || itemName.toUpperCase().replace(/\s/g, '_');
    }

    determineItemStatus(itemName, itemValue, referenceRange) {
        const numericValue = parseFloat(itemValue.split('/')[0]);

        if (itemName.includes('콜레스테롤')) {
            if (numericValue > 240) return 'ABNORMAL';
            if (numericValue > 200) return 'ABNORMAL';
            return 'NORMAL';
        }

        if (itemName.includes('혈당') || itemName.includes('혈당')) {
            if (numericValue > 126) return 'ABNORMAL';
            if (numericValue > 100) return 'ABNORMAL';
            return 'NORMAL';
        }

        if (itemName.includes('혈압')) {
            const values = itemValue.split('/');
            const systolic = parseFloat(values[0]);
            const diastolic = parseFloat(values[1] || values[0]);
            if (systolic > 140 || diastolic > 90) return 'ABNORMAL';
            return 'NORMAL';
        }

        if (referenceRange) {
            if (referenceRange.includes('이하') || referenceRange.includes('미만')) {
                const max = parseFloat(referenceRange.match(/\d+/)?.[0]);
                if (max && numericValue > max) return 'ABNORMAL';
            }
            if (referenceRange.includes('이상')) {
                const min = parseFloat(referenceRange.match(/\d+/)?.[0]);
                if (min && numericValue < min) return 'ABNORMAL';
            }
        }

        return 'NORMAL';
    }

    getCheckupTypeName(type) {
        const typeMap = {
            'GENERAL': '일반검진',
            'CANCER': '암검진',
            'CARDIOVASCULAR': '심혈관검진',
            'METABOLIC': '대사질환검진',
            'OTHER': '기타'
        };
        return typeMap[type] || type;
    }

    calculateRiskLevel(checkup) {
        const abnormalCount = checkup.items.filter(item => item.status === 'ABNORMAL').length;
        const totalCount = checkup.items.length;
        const abnormalRatio = abnormalCount / totalCount;

        if (abnormalRatio >= 0.5 || abnormalCount >= 3) {
            return { text: '위험', class: 'risk-high', icon: 'fa-exclamation-circle' };
        } else if (abnormalRatio >= 0.3 || abnormalCount >= 2) {
            return { text: '높음', class: 'risk-medium', icon: 'fa-exclamation-triangle' };
        } else if (abnormalCount >= 1) {
            return { text: '중간', class: 'risk-low', icon: 'fa-info-circle' };
        } else {
            return { text: '낮음', class: 'risk-normal', icon: 'fa-check-circle' };
        }
    }

    showCheckupDetailModal(checkup) {
        const header = document.getElementById('checkup-detail-header');
        const itemsDetail = document.getElementById('checkup-items-detail');
        const riskLevel = this.calculateRiskLevel(checkup);

        header.innerHTML = `
            <div class="checkup-detail-title">
                <h4>${this.getCheckupTypeName(checkup.checkupType)}</h4>
                <div class="checkup-detail-meta">
                    <span><i class="fas fa-calendar"></i> ${checkup.checkupDate}</span>
                    <span><i class="fas fa-hospital"></i> ${checkup.hospitalName}</span>
                </div>
            </div>
            <div class="checkup-detail-risk ${riskLevel.class}">
                <i class="fas ${riskLevel.icon}"></i>
                <span>위험도: ${riskLevel.text}</span>
            </div>
        `;

        itemsDetail.innerHTML = `
            <h4>검진 항목 상세</h4>
            <div class="checkup-items-list">
                ${checkup.items.map(item => `
                    <div class="checkup-item-detail ${item.status === 'ABNORMAL' ? 'abnormal' : ''}">
                        <div class="item-detail-header">
                            <span class="item-detail-name">${item.itemName}</span>
                            <span class="item-detail-status ${item.status === 'ABNORMAL' ? 'abnormal' : 'normal'}">
                                ${item.status === 'ABNORMAL' ? '이상' : '정상'}
                            </span>
                        </div>
                        <div class="item-detail-body">
                            <div class="item-detail-value">
                                <span class="value">${item.itemValue}</span>
                                <span class="unit">${item.unit}</span>
                            </div>
                            ${item.referenceRange ? `
                                <div class="item-detail-reference">
                                    <span>기준 범위: ${item.referenceRange}</span>
                                </div>
                            ` : ''}
                        </div>
                    </div>
                `).join('')}
            </div>
        `;

        document.getElementById('view-checkup-trend').dataset.checkupSeq = checkup.checkupSeq;
        document.getElementById('checkup-detail-modal').classList.remove('hidden');
    }

    hideCheckupDetailModal() {
        document.getElementById('checkup-detail-modal').classList.add('hidden');
    }

    showTrendModal() {
        const checkupSeq = parseInt(document.getElementById('view-checkup-trend').dataset.checkupSeq);
        const currentCheckup = this.checkupData.find(c => c.checkupSeq === checkupSeq);

        if (!currentCheckup) return;

        const allItemCodes = new Set();
        this.checkupData.forEach(checkup => {
            checkup.items.forEach(item => {
                allItemCodes.add(item.itemCode);
            });
        });

        const select = document.getElementById('trend-item-select');
        select.innerHTML = '<option value="">항목 선택</option>';
        allItemCodes.forEach(code => {
            const item = currentCheckup.items.find(i => i.itemCode === code) || 
                        this.checkupData.flatMap(c => c.items).find(i => i.itemCode === code);
            if (item) {
                const option = document.createElement('option');
                option.value = code;
                option.textContent = item.itemName;
                select.appendChild(option);
            }
        });

        if (allItemCodes.size > 0) {
            select.value = Array.from(allItemCodes)[0];
            this.updateTrendChart();
        }

        document.getElementById('checkup-trend-modal').classList.remove('hidden');
    }

    hideTrendModal() {
        document.getElementById('checkup-trend-modal').classList.add('hidden');
    }

    updateTrendChart() {
        const itemCode = document.getElementById('trend-item-select').value;
        const canvas = document.getElementById('trend-chart');
        if (!canvas) return;

        const container = document.getElementById('trend-chart-container');
        const containerWidth = container ? container.clientWidth - 32 : 800;
        const containerHeight = 500;
        
        canvas.width = containerWidth;
        canvas.height = containerHeight;

        const ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        if (!itemCode) {
            ctx.fillStyle = '#999';
            ctx.font = '18px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
            ctx.textAlign = 'center';
            ctx.fillText('항목을 선택해주세요.', canvas.width / 2, canvas.height / 2);
            return;
        }

        const trendData = [];
        this.checkupData.forEach(checkup => {
            const item = checkup.items.find(i => i.itemCode === itemCode);
            if (item) {
                let numericValue = parseFloat(item.itemValue.split('/')[0]);
                if (isNaN(numericValue)) {
                    numericValue = parseFloat(item.itemValue);
                }
                if (!isNaN(numericValue)) {
                    trendData.push({
                        date: checkup.checkupDate,
                        value: numericValue,
                        status: item.status,
                        unit: item.unit,
                        itemName: item.itemName
                    });
                }
            }
        });

        if (trendData.length === 0) {
            ctx.fillStyle = '#999';
            ctx.font = '18px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
            ctx.textAlign = 'center';
            ctx.fillText('추이 데이터가 없습니다.', canvas.width / 2, canvas.height / 2);
            return;
        }

        trendData.sort((a, b) => new Date(a.date) - new Date(b.date));

        const width = canvas.width;
        const height = canvas.height;
        const padding = { top: 60, right: 40, bottom: 80, left: 85 };
        const chartWidth = width - padding.left - padding.right;
        const chartHeight = height - padding.top - padding.bottom;

        const values = trendData.map(d => d.value);
        const minValue = Math.max(0, Math.min(...values) * 0.85);
        const maxValue = Math.max(...values) * 1.15;
        const valueRange = maxValue - minValue || 1;

        ctx.fillStyle = '#ffffff';
        ctx.fillRect(0, 0, width, height);

        ctx.fillStyle = '#f8f9fa';
        ctx.fillRect(padding.left, padding.top, chartWidth, chartHeight);

        ctx.strokeStyle = '#e8e8e8';
        ctx.lineWidth = 1;
        const gridLines = 6;
        for (let i = 0; i <= gridLines; i++) {
            const y = padding.top + (chartHeight / gridLines) * i;
            ctx.beginPath();
            ctx.moveTo(padding.left, y);
            ctx.lineTo(padding.left + chartWidth, y);
            ctx.stroke();

            const value = maxValue - (valueRange / gridLines) * i;
            ctx.fillStyle = '#666';
            ctx.font = '12px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
            ctx.textAlign = 'right';
            const formattedValue = value % 1 === 0 ? value.toFixed(0) : value.toFixed(1);
            ctx.fillText(formattedValue, padding.left - 15, y + 4);
        }

        ctx.strokeStyle = '#333';
        ctx.lineWidth = 2.5;
        ctx.beginPath();
        ctx.moveTo(padding.left, padding.top);
        ctx.lineTo(padding.left, padding.top + chartHeight);
        ctx.stroke();

        ctx.beginPath();
        ctx.moveTo(padding.left, padding.top + chartHeight);
        ctx.lineTo(padding.left + chartWidth, padding.top + chartHeight);
        ctx.stroke();


        if (trendData.length === 1) {
            const data = trendData[0];
            const x = padding.left + chartWidth / 2;
            const y = padding.top + chartHeight - ((data.value - minValue) / valueRange) * chartHeight;

            const pointColor = data.status === 'ABNORMAL' ? '#F44336' : '#4CAF50';
            
            ctx.shadowColor = 'rgba(0, 0, 0, 0.2)';
            ctx.shadowBlur = 4;
            ctx.shadowOffsetX = 0;
            ctx.shadowOffsetY = 2;
            
            ctx.fillStyle = pointColor;
            ctx.beginPath();
            ctx.arc(x, y, 10, 0, Math.PI * 2);
            ctx.fill();

            ctx.strokeStyle = '#fff';
            ctx.lineWidth = 3;
            ctx.stroke();

            ctx.shadowBlur = 0;

            ctx.fillStyle = '#333';
            ctx.font = 'bold 14px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
            ctx.textAlign = 'center';
            const valueText = data.value % 1 === 0 ? data.value.toFixed(0) : data.value.toFixed(1);
            ctx.fillText(valueText + (data.unit ? ' ' + data.unit : ''), x, y - 18);
            
            ctx.font = '13px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
            const dateObj = new Date(data.date);
            const dateStr = `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')}`;
            ctx.fillText(dateStr, x, height - padding.bottom + 25);
        } else {
            const gradient = ctx.createLinearGradient(padding.left, padding.top, padding.left, padding.top + chartHeight);
            gradient.addColorStop(0, 'rgba(33, 150, 243, 0.3)');
            gradient.addColorStop(1, 'rgba(33, 150, 243, 0.05)');

            ctx.strokeStyle = '#2196F3';
            ctx.lineWidth = 3.5;
            ctx.lineCap = 'round';
            ctx.lineJoin = 'round';
            ctx.shadowColor = 'rgba(33, 150, 243, 0.3)';
            ctx.shadowBlur = 8;
            ctx.shadowOffsetX = 0;
            ctx.shadowOffsetY = 2;

            ctx.beginPath();
            trendData.forEach((data, index) => {
                const x = padding.left + (index / (trendData.length - 1)) * chartWidth;
                const y = padding.top + chartHeight - ((data.value - minValue) / valueRange) * chartHeight;

                if (index === 0) {
                    ctx.moveTo(x, y);
                } else {
                    ctx.lineTo(x, y);
                }
            });
            ctx.stroke();

            ctx.shadowBlur = 0;

            ctx.fillStyle = gradient;
            ctx.beginPath();
            trendData.forEach((data, index) => {
                const x = padding.left + (index / (trendData.length - 1)) * chartWidth;
                const y = padding.top + chartHeight - ((data.value - minValue) / valueRange) * chartHeight;
                if (index === 0) {
                    ctx.moveTo(x, padding.top + chartHeight);
                    ctx.lineTo(x, y);
                } else {
                    ctx.lineTo(x, y);
                }
            });
            const lastX = padding.left + chartWidth;
            ctx.lineTo(lastX, padding.top + chartHeight);
            ctx.closePath();
            ctx.fill();

            trendData.forEach((data, index) => {
                const x = padding.left + (index / (trendData.length - 1)) * chartWidth;
                const y = padding.top + chartHeight - ((data.value - minValue) / valueRange) * chartHeight;

                const pointColor = data.status === 'ABNORMAL' ? '#F44336' : '#4CAF50';
                
                ctx.shadowColor = 'rgba(0, 0, 0, 0.2)';
                ctx.shadowBlur = 4;
                ctx.shadowOffsetX = 0;
                ctx.shadowOffsetY = 2;

                ctx.fillStyle = pointColor;
                ctx.beginPath();
                ctx.arc(x, y, 10, 0, Math.PI * 2);
                ctx.fill();

                ctx.strokeStyle = '#fff';
                ctx.lineWidth = 3;
                ctx.stroke();

                ctx.shadowBlur = 0;

                ctx.fillStyle = '#333';
                ctx.font = 'bold 13px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
                
                const valueText = data.value % 1 === 0 ? data.value.toFixed(0) : data.value.toFixed(1);
                const valueLabel = valueText + (data.unit ? ' ' + data.unit : '');
                
                if (index === 0 && x < padding.left + 30) {
                    ctx.textAlign = 'left';
                    ctx.fillText(valueLabel, x + 15, y - 18);
                } else if (index === trendData.length - 1 && x > width - padding.right - 30) {
                    ctx.textAlign = 'right';
                    ctx.fillText(valueLabel, x - 15, y - 18);
                } else {
                    ctx.textAlign = 'center';
                    ctx.fillText(valueLabel, x, y - 18);
                }

                ctx.font = '12px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
                const dateObj = new Date(data.date);
                const dateStr = `${String(dateObj.getMonth() + 1).padStart(2, '0')}/${String(dateObj.getDate()).padStart(2, '0')}`;
                ctx.fillText(dateStr, x, height - padding.bottom + 25);
            });
        }

        ctx.fillStyle = '#1a1a1a';
        ctx.font = 'bold 20px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText(`${trendData[0].itemName} 추이 분석`, width / 2, 35);

        const legendY = height - 35;
        ctx.font = '13px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
        ctx.textAlign = 'left';

        ctx.fillStyle = '#4CAF50';
        ctx.beginPath();
        ctx.arc(40, legendY, 7, 0, Math.PI * 2);
        ctx.fill();
        ctx.strokeStyle = '#fff';
        ctx.lineWidth = 2;
        ctx.stroke();
        ctx.fillStyle = '#333';
        ctx.fillText('정상', 55, legendY + 5);

        ctx.fillStyle = '#F44336';
        ctx.beginPath();
        ctx.arc(120, legendY, 7, 0, Math.PI * 2);
        ctx.fill();
        ctx.strokeStyle = '#fff';
        ctx.lineWidth = 2;
        ctx.stroke();
        ctx.fillStyle = '#333';
        ctx.fillText('이상', 135, legendY + 5);

        if (trendData[0].unit) {
            ctx.fillStyle = '#666';
            ctx.font = '12px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
            ctx.textAlign = 'right';
            ctx.fillText(`단위: ${trendData[0].unit}`, width - 40, legendY + 5);
        }
    }

    detectAbnormalIndicators() {
        if (this.checkupData.length === 0) return;

        const latestCheckup = this.checkupData[0];
        const abnormalItems = latestCheckup.items.filter(item => item.status === 'ABNORMAL');

        if (abnormalItems.length > 0) {
            const riskLevel = this.calculateRiskLevel(latestCheckup);
            let message = `건강검진 결과에서 이상 지표 ${abnormalItems.length}개가 발견되었습니다.`;

            if (riskLevel.class === 'risk-high') {
                message += ' 의사 상담을 권장합니다.';
                this.showToast(message, 'error');
            } else if (riskLevel.class === 'risk-medium') {
                message += ' 주의가 필요합니다.';
                this.showToast(message, 'warning');
            } else {
                this.showToast(message, 'info');
            }
        }
    }

    initializeFoodDatabase() {
        return {
            '현미밥': { calories: 150, protein: 3, carbs: 33, fat: 1, fiber: 1, unit: '1공기(210g)' },
            '계란후라이': { calories: 90, protein: 6, carbs: 0, fat: 7, fiber: 0, unit: '1개' },
            '시금치나물': { calories: 30, protein: 2, carbs: 4, fat: 0, fiber: 2, unit: '1인분' },
            '닭가슴살': { calories: 165, protein: 31, carbs: 0, fat: 3.6, fiber: 0, unit: '100g' },
            '샐러드': { calories: 50, protein: 2, carbs: 8, fat: 1, fiber: 3, unit: '1인분' },
            '고구마': { calories: 130, protein: 2, carbs: 30, fat: 0, fiber: 4, unit: '1개(200g)' },
            '연어구이': { calories: 206, protein: 25, carbs: 0, fat: 10, fiber: 0, unit: '100g' },
            '브로콜리': { calories: 35, protein: 3, carbs: 6, fat: 0, fiber: 2.5, unit: '100g' },
            '바나나': { calories: 105, protein: 1, carbs: 27, fat: 0, fiber: 3, unit: '1개(118g)' },
            '사과': { calories: 95, protein: 0, carbs: 25, fat: 0, fiber: 4, unit: '1개(182g)' },
            '오트밀': { calories: 150, protein: 5, carbs: 27, fat: 3, fiber: 4, unit: '1컵(234g)' },
            '그릭요거트': { calories: 100, protein: 17, carbs: 6, fat: 0, fiber: 0, unit: '1컵(170g)' },
            '견과류': { calories: 200, protein: 5, carbs: 6, fat: 18, fiber: 3, unit: '30g' },
            '두부': { calories: 76, protein: 8, carbs: 2, fat: 4.8, fiber: 0, unit: '100g' },
            '김치': { calories: 15, protein: 1, carbs: 2, fat: 0, fiber: 1, unit: '1인분' },
            '된장국': { calories: 40, protein: 2, carbs: 5, fat: 1, fiber: 1, unit: '1인분' },
            '불고기': { calories: 250, protein: 25, carbs: 5, fat: 15, fiber: 0, unit: '100g' },
            '잡채': { calories: 180, protein: 5, carbs: 25, fat: 7, fiber: 2, unit: '1인분' },
            '미역국': { calories: 25, protein: 1, carbs: 3, fat: 1, fiber: 1, unit: '1인분' },
            '콩나물무침': { calories: 35, protein: 2, carbs: 5, fat: 1, fiber: 2, unit: '1인분' }
        };
    }

    calculateCalorieNeeds(goal, activityLevel) {
        if (!this.currentUser || !this.currentUser.height || !this.currentUser.weight) {
            return 2000;
        }

        const height = this.currentUser.height;
        const weight = this.currentUser.weight;
        const age = this.currentUser.birthEnc ? 
            new Date().getFullYear() - parseInt(this.currentUser.birthEnc.split('-')[0]) : 30;
        const gender = this.currentUser.gender === 'M' ? 'male' : 'female';

        let bmr;
        if (gender === 'male') {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }

        const activityMultipliers = {
            'SEDENTARY': 1.2,
            'LIGHT': 1.375,
            'MODERATE': 1.55,
            'ACTIVE': 1.725,
            'VERY_ACTIVE': 1.9
        };

        const tdee = bmr * (activityMultipliers[activityLevel] || 1.55);

        const goalAdjustments = {
            'WEIGHT_LOSS': -500,
            'WEIGHT_MAINTENANCE': 0,
            'WEIGHT_GAIN': 500,
            'MUSCLE_GAIN': 300
        };

        return Math.round(tdee + (goalAdjustments[goal] || 0));
    }

    generateDietPlan() {
        const goal = document.getElementById('diet-goal').value;
        const activityLevel = document.getElementById('activity-level').value;
        const duration = parseInt(document.getElementById('diet-duration').value);
        const healthConditions = Array.from(document.querySelectorAll('input[name="health-conditions"]:checked'))
            .map(cb => cb.value);

        if (!goal || !activityLevel || !duration) {
            this.showToast('필수 정보를 모두 입력해주세요.', 'error');
            return;
        }

        const dailyCalories = this.calculateCalorieNeeds(goal, activityLevel);
        const proteinRatio = goal === 'MUSCLE_GAIN' ? 0.3 : 0.25;
        const carbRatio = 0.45;
        const fatRatio = 0.30;

        const dailyProtein = Math.round(dailyCalories * proteinRatio / 4);
        const dailyCarbs = Math.round(dailyCalories * carbRatio / 4);
        const dailyFat = Math.round(dailyCalories * fatRatio / 9);

        const mealCalories = {
            'BREAKFAST': Math.round(dailyCalories * 0.25),
            'LUNCH': Math.round(dailyCalories * 0.35),
            'DINNER': Math.round(dailyCalories * 0.30),
            'SNACK': Math.round(dailyCalories * 0.10)
        };

        const days = [];
        for (let i = 0; i < duration; i++) {
            const date = new Date();
            date.setDate(date.getDate() + i);
            const dateStr = date.toISOString().split('T')[0];

            const dayPlan = {
                date: dateStr,
                meals: []
            };

            ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'].forEach(mealType => {
                const meal = this.generateMeal(mealType, mealCalories[mealType], healthConditions);
                dayPlan.meals.push(meal);
            });

            days.push(dayPlan);
        }

        this.dietPlan = {
            planSeq: Date.now(),
            userId: this.currentUser.userId,
            goal: goal,
            activityLevel: activityLevel,
            startDate: days[0].date,
            endDate: days[days.length - 1].date,
            dailyCalories: dailyCalories,
            dailyProtein: dailyProtein,
            dailyCarbs: dailyCarbs,
            dailyFat: dailyFat,
            healthConditions: healthConditions,
            days: days
        };

        this.nutritionGoals = {
            dailyCalories: dailyCalories,
            dailyProtein: dailyProtein,
            dailyCarbs: dailyCarbs,
            dailyFat: dailyFat,
            dailyFiber: 25
        };

        this.saveToStorage();
        this.hideDietPlanModal();
        this.updateDietPlan();
        this.showToast('식단 계획이 생성되었습니다!', 'success');
    }

    generateMeal(mealType, targetCalories, healthConditions) {
        const mealNames = {
            'BREAKFAST': ['현미밥', '계란후라이', '시금치나물', '미역국'],
            'LUNCH': ['현미밥', '닭가슴살', '샐러드', '고구마', '김치'],
            'DINNER': ['현미밥', '연어구이', '브로콜리', '된장국', '콩나물무침'],
            'SNACK': ['사과', '그릭요거트', '견과류']
        };

        let availableFoods = mealNames[mealType] || [];

        if (healthConditions.includes('HYPERTENSION')) {
            availableFoods = availableFoods.filter(f => !['불고기', '잡채'].includes(f));
            availableFoods.push('바나나');
        }
        if (healthConditions.includes('DIABETES')) {
            availableFoods = availableFoods.filter(f => !['고구마', '사과'].includes(f));
        }
        if (healthConditions.includes('HIGH_CHOLESTEROL')) {
            availableFoods = availableFoods.filter(f => !['연어구이'].includes(f));
        }

        const meal = {
            mealType: mealType,
            items: [],
            totalCalories: 0,
            totalProtein: 0,
            totalCarbs: 0,
            totalFat: 0,
            totalFiber: 0
        };

        let currentCalories = 0;
        const selectedFoods = [];

        while (currentCalories < targetCalories * 0.9 && selectedFoods.length < 5) {
            const foodName = availableFoods[Math.floor(Math.random() * availableFoods.length)];
            if (selectedFoods.includes(foodName)) continue;

            const food = this.foodDatabase[foodName];
            if (!food) continue;

            const quantity = mealType === 'SNACK' ? 1 : (Math.random() * 0.5 + 0.8);
            const calories = Math.round(food.calories * quantity);

            if (currentCalories + calories > targetCalories * 1.1) break;

            meal.items.push({
                foodName: foodName,
                quantity: quantity,
                unit: food.unit,
                calories: calories,
                protein: Math.round(food.protein * quantity),
                carbs: Math.round(food.carbs * quantity),
                fat: Math.round(food.fat * quantity),
                fiber: Math.round(food.fiber * quantity)
            });

            meal.totalCalories += calories;
            meal.totalProtein += Math.round(food.protein * quantity);
            meal.totalCarbs += Math.round(food.carbs * quantity);
            meal.totalFat += Math.round(food.fat * quantity);
            meal.totalFiber += Math.round(food.fiber * quantity);

            currentCalories += calories;
            selectedFoods.push(foodName);
        }

        return meal;
    }

    updateDietScreen() {
        this.updateDietPlan();
        this.updateMealRecords();
        this.updateNutritionAnalysis();
    }

    updateDietPlan() {
        const summaryContainer = document.getElementById('diet-plan-summary');
        const daysContainer = document.getElementById('diet-plan-days');

        if (!this.dietPlan) {
            summaryContainer.innerHTML = `
                <div class="no-data-message">
                    <i class="fas fa-utensils"></i>
                    <p>식단 계획이 없습니다.</p>
                    <p>맞춤 식단 계획을 생성해보세요!</p>
                </div>
            `;
            daysContainer.innerHTML = '';
            return;
        }

        const goalNames = {
            'WEIGHT_LOSS': '체중 감량',
            'WEIGHT_MAINTENANCE': '체중 유지',
            'WEIGHT_GAIN': '체중 증가',
            'MUSCLE_GAIN': '근육 증가'
        };

        summaryContainer.innerHTML = `
            <div class="diet-summary-card">
                <h3>식단 계획 요약</h3>
                <div class="summary-grid">
                    <div class="summary-item">
                        <span class="summary-label">목표</span>
                        <span class="summary-value">${goalNames[this.dietPlan.goal]}</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">기간</span>
                        <span class="summary-value">${this.dietPlan.startDate} ~ ${this.dietPlan.endDate}</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">일일 칼로리</span>
                        <span class="summary-value">${this.dietPlan.dailyCalories} kcal</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">단백질</span>
                        <span class="summary-value">${this.dietPlan.dailyProtein}g</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">탄수화물</span>
                        <span class="summary-value">${this.dietPlan.dailyCarbs}g</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">지방</span>
                        <span class="summary-value">${this.dietPlan.dailyFat}g</span>
                    </div>
                </div>
            </div>
        `;

        daysContainer.innerHTML = '';
        this.dietPlan.days.forEach((day, index) => {
            const dayCard = document.createElement('div');
            dayCard.className = 'diet-day-card';

            const mealNames = {
                'BREAKFAST': '아침',
                'LUNCH': '점심',
                'DINNER': '저녁',
                'SNACK': '간식'
            };

            dayCard.innerHTML = `
                <div class="day-header">
                    <h4>${index + 1}일차 - ${day.date}</h4>
                    <span class="day-total-calories">총 ${day.meals.reduce((sum, m) => sum + m.totalCalories, 0)} kcal</span>
                </div>
                <div class="day-meals">
                    ${day.meals.map(meal => `
                        <div class="meal-card">
                            <div class="meal-header">
                                <h5>${mealNames[meal.mealType]}</h5>
                                <span class="meal-calories">${meal.totalCalories} kcal</span>
                            </div>
                            <div class="meal-items">
                                ${meal.items.map(item => `
                                    <div class="meal-item">
                                        <span class="item-name">${item.foodName}</span>
                                        <span class="item-quantity">${item.quantity.toFixed(1)} ${item.unit}</span>
                                        <span class="item-calories">${item.calories} kcal</span>
                                    </div>
                                `).join('')}
                            </div>
                            <div class="meal-nutrition">
                                <span>단백질: ${meal.totalProtein}g</span>
                                <span>탄수화물: ${meal.totalCarbs}g</span>
                                <span>지방: ${meal.totalFat}g</span>
                            </div>
                        </div>
                    `).join('')}
                </div>
            `;
            daysContainer.appendChild(dayCard);
        });
    }

    showDietPlanModal() {
        document.getElementById('diet-plan-modal').classList.remove('hidden');
        document.getElementById('diet-goal').value = '';
        document.getElementById('activity-level').value = '';
        document.getElementById('diet-duration').value = '7';
        document.querySelectorAll('input[name="health-conditions"]').forEach(cb => cb.checked = false);
    }

    hideDietPlanModal() {
        document.getElementById('diet-plan-modal').classList.add('hidden');
    }

    showMealRecordModal() {
        document.getElementById('meal-record-modal').classList.remove('hidden');
        document.getElementById('meal-date').value = new Date().toISOString().split('T')[0];
        document.getElementById('meal-type').value = '';
        document.getElementById('meal-time').value = '';
        document.getElementById('food-items-container').innerHTML = '';
        this.addFoodItem();
    }

    hideMealRecordModal() {
        document.getElementById('meal-record-modal').classList.add('hidden');
    }

    addFoodItem() {
        const container = document.getElementById('food-items-container');
        const itemDiv = document.createElement('div');
        itemDiv.className = 'food-item-input';
        itemDiv.innerHTML = `
            <div class="input-row">
                <div class="input-group">
                    <label>음식명 *</label>
                    <input type="text" class="food-name" list="food-list" placeholder="음식명 입력" required />
                    <datalist id="food-list">
                        ${Object.keys(this.foodDatabase).map(food => `<option value="${food}">${food}</option>`).join('')}
                    </datalist>
                </div>
                <div class="input-group">
                    <label>수량 *</label>
                    <input type="number" class="food-quantity" step="0.1" min="0.1" value="1" required />
                </div>
            </div>
            <div class="input-group">
                <label>단위</label>
                <input type="text" class="food-unit" placeholder="예: 1공기, 100g" />
            </div>
            <button type="button" class="remove-item-button" onclick="this.parentElement.remove(); window.healthcareApp.calculateMealTotal();">
                <i class="fas fa-times"></i> 제거
            </button>
        `;
        container.appendChild(itemDiv);

        itemDiv.querySelector('.food-name').addEventListener('change', () => {
            this.updateFoodItemNutrition(itemDiv);
        });
        itemDiv.querySelector('.food-quantity').addEventListener('input', () => {
            this.updateFoodItemNutrition(itemDiv);
        });
    }

    updateFoodItemNutrition(itemDiv) {
        const foodName = itemDiv.querySelector('.food-name').value;
        const quantity = parseFloat(itemDiv.querySelector('.food-quantity').value) || 1;
        const food = this.foodDatabase[foodName];

        if (food) {
            const unitInput = itemDiv.querySelector('.food-unit');
            if (!unitInput.value) {
                unitInput.value = food.unit;
            }
        }
    }

    submitMealRecord() {
        const mealDate = document.getElementById('meal-date').value;
        const mealType = document.getElementById('meal-type').value;
        const mealTime = document.getElementById('meal-time').value;

        if (!mealDate || !mealType) {
            this.showToast('필수 정보를 입력해주세요.', 'error');
            return;
        }

        const foodItems = [];
        const foodInputs = document.querySelectorAll('.food-item-input');

        foodInputs.forEach(input => {
            const foodName = input.querySelector('.food-name').value;
            const quantity = parseFloat(input.querySelector('.food-quantity').value) || 1;
            const unit = input.querySelector('.food-unit').value || '';

            if (!foodName) {
                this.showToast('모든 음식의 이름을 입력해주세요.', 'error');
                return;
            }

            const food = this.foodDatabase[foodName];
            if (!food) {
                this.showToast(`${foodName}의 영양 정보가 없습니다.`, 'warning');
            }

            const calories = food ? Math.round(food.calories * quantity) : 0;
            const protein = food ? Math.round(food.protein * quantity) : 0;
            const carbs = food ? Math.round(food.carbs * quantity) : 0;
            const fat = food ? Math.round(food.fat * quantity) : 0;
            const fiber = food ? Math.round(food.fiber * quantity) : 0;

            foodItems.push({
                foodName: foodName,
                quantity: quantity,
                unit: unit || (food ? food.unit : ''),
                calories: calories,
                protein: protein,
                carbs: carbs,
                fat: fat,
                fiber: fiber
            });
        });

        if (foodItems.length === 0) {
            this.showToast('최소 1개 이상의 음식을 입력해주세요.', 'error');
            return;
        }

        const totalCalories = foodItems.reduce((sum, item) => sum + item.calories, 0);
        const totalProtein = foodItems.reduce((sum, item) => sum + item.protein, 0);
        const totalCarbs = foodItems.reduce((sum, item) => sum + item.carbs, 0);
        const totalFat = foodItems.reduce((sum, item) => sum + item.fat, 0);
        const totalFiber = foodItems.reduce((sum, item) => sum + item.fiber, 0);

        const mealRecord = {
            recordSeq: Date.now(),
            userId: this.currentUser.userId,
            recordDate: mealDate,
            mealType: mealType,
            mealTime: mealTime || null,
            items: foodItems,
            totalCalories: totalCalories,
            totalProtein: totalProtein,
            totalCarbs: totalCarbs,
            totalFat: totalFat,
            totalFiber: totalFiber
        };

        this.mealRecords.unshift(mealRecord);
        this.mealRecords.sort((a, b) => {
            const dateCompare = new Date(b.recordDate) - new Date(a.recordDate);
            if (dateCompare !== 0) return dateCompare;
            const typeOrder = { 'BREAKFAST': 1, 'LUNCH': 2, 'DINNER': 3, 'SNACK': 4 };
            return (typeOrder[a.mealType] || 5) - (typeOrder[b.mealType] || 5);
        });

        this.saveToStorage();
        this.hideMealRecordModal();
        this.updateMealRecords();
        this.updateNutritionAnalysis();
        this.showToast('식사 기록이 저장되었습니다.', 'success');
    }

    updateMealRecords() {
        const container = document.getElementById('meal-records-list');

        if (this.mealRecords.length === 0) {
            container.innerHTML = `
                <div class="no-data-message">
                    <i class="fas fa-utensils"></i>
                    <p>식사 기록이 없습니다.</p>
                    <p>식사를 기록해보세요!</p>
                </div>
            `;
            return;
        }

        container.innerHTML = '';
        const groupedByDate = {};

        this.mealRecords.forEach(record => {
            if (!groupedByDate[record.recordDate]) {
                groupedByDate[record.recordDate] = [];
            }
            groupedByDate[record.recordDate].push(record);
        });

        Object.keys(groupedByDate).sort((a, b) => new Date(b) - new Date(a)).forEach(date => {
            const dateCard = document.createElement('div');
            dateCard.className = 'meal-date-card';

            const dayRecords = groupedByDate[date];
            const dayTotal = dayRecords.reduce((sum, r) => sum + r.totalCalories, 0);

            const mealNames = {
                'BREAKFAST': '아침',
                'LUNCH': '점심',
                'DINNER': '저녁',
                'SNACK': '간식'
            };

            dateCard.innerHTML = `
                <div class="date-header">
                    <h4>${date}</h4>
                    <span class="date-total">총 ${dayTotal} kcal</span>
                </div>
                <div class="date-meals">
                    ${dayRecords.map(record => `
                        <div class="meal-record-card">
                            <div class="meal-record-header">
                                <span class="meal-type-badge">${mealNames[record.mealType]}</span>
                                ${record.mealTime ? `<span class="meal-time">${record.mealTime}</span>` : ''}
                                <span class="meal-calories">${record.totalCalories} kcal</span>
                            </div>
                            <div class="meal-record-items">
                                ${record.items.map(item => `
                                    <div class="record-item">
                                        <span class="item-name">${item.foodName}</span>
                                        <span class="item-details">${item.quantity}${item.unit ? ' ' + item.unit : ''}</span>
                                        <span class="item-calories">${item.calories} kcal</span>
                                    </div>
                                `).join('')}
                            </div>
                            <div class="meal-record-nutrition">
                                <span>단백질: ${record.totalProtein}g</span>
                                <span>탄수화물: ${record.totalCarbs}g</span>
                                <span>지방: ${record.totalFat}g</span>
                            </div>
                        </div>
                    `).join('')}
                </div>
            `;
            container.appendChild(dateCard);
        });
    }

    updateNutritionAnalysis() {
        if (!this.nutritionGoals) {
            document.getElementById('nutrition-goals').innerHTML = `
                <div class="no-data-message">
                    <p>영양 목표를 설정하려면 식단 계획을 먼저 생성해주세요.</p>
                </div>
            `;
            document.getElementById('nutrition-progress').innerHTML = '';
            return;
        }

        const today = new Date().toISOString().split('T')[0];
        const todayRecords = this.mealRecords.filter(r => r.recordDate === today);

        const todayTotal = {
            calories: todayRecords.reduce((sum, r) => sum + r.totalCalories, 0),
            protein: todayRecords.reduce((sum, r) => sum + r.totalProtein, 0),
            carbs: todayRecords.reduce((sum, r) => sum + r.totalCarbs, 0),
            fat: todayRecords.reduce((sum, r) => sum + r.totalFat, 0),
            fiber: todayRecords.reduce((sum, r) => sum + r.totalFiber, 0)
        };

        const goals = this.nutritionGoals;
        const compliance = {
            calories: Math.min(100, Math.round((todayTotal.calories / goals.dailyCalories) * 100)),
            protein: Math.min(100, Math.round((todayTotal.protein / goals.dailyProtein) * 100)),
            carbs: Math.min(100, Math.round((todayTotal.carbs / goals.dailyCarbs) * 100)),
            fat: Math.min(100, Math.round((todayTotal.fat / goals.dailyFat) * 100)),
            fiber: Math.min(100, Math.round((todayTotal.fiber / goals.dailyFiber) * 100))
        };

        document.getElementById('nutrition-goals').innerHTML = `
            <div class="nutrition-goals-card">
                <h3>오늘의 영양소 목표</h3>
                <div class="goals-grid">
                    <div class="goal-item">
                        <span class="goal-label">칼로리</span>
                        <span class="goal-value">${goals.dailyCalories} kcal</span>
                    </div>
                    <div class="goal-item">
                        <span class="goal-label">단백질</span>
                        <span class="goal-value">${goals.dailyProtein}g</span>
                    </div>
                    <div class="goal-item">
                        <span class="goal-label">탄수화물</span>
                        <span class="goal-value">${goals.dailyCarbs}g</span>
                    </div>
                    <div class="goal-item">
                        <span class="goal-label">지방</span>
                        <span class="goal-value">${goals.dailyFat}g</span>
                    </div>
                    <div class="goal-item">
                        <span class="goal-label">섬유질</span>
                        <span class="goal-value">${goals.dailyFiber}g</span>
                    </div>
                </div>
            </div>
        `;

        document.getElementById('nutrition-progress').innerHTML = `
            <div class="nutrition-progress-card">
                <h3>오늘의 섭취량 및 준수도</h3>
                <div class="progress-items">
                    <div class="progress-item">
                        <div class="progress-header">
                            <span class="progress-label">칼로리</span>
                            <span class="progress-values">${todayTotal.calories} / ${goals.dailyCalories} kcal</span>
                        </div>
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${compliance.calories}%; background: ${compliance.calories > 100 ? '#F44336' : compliance.calories >= 80 ? '#4CAF50' : '#FF9800'};"></div>
                        </div>
                        <span class="progress-percent">${compliance.calories}%</span>
                    </div>
                    <div class="progress-item">
                        <div class="progress-header">
                            <span class="progress-label">단백질</span>
                            <span class="progress-values">${todayTotal.protein} / ${goals.dailyProtein}g</span>
                        </div>
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${compliance.protein}%; background: ${compliance.protein >= 80 ? '#4CAF50' : '#FF9800'};"></div>
                        </div>
                        <span class="progress-percent">${compliance.protein}%</span>
                    </div>
                    <div class="progress-item">
                        <div class="progress-header">
                            <span class="progress-label">탄수화물</span>
                            <span class="progress-values">${todayTotal.carbs} / ${goals.dailyCarbs}g</span>
                        </div>
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${compliance.carbs}%; background: ${compliance.carbs >= 80 ? '#4CAF50' : '#FF9800'};"></div>
                        </div>
                        <span class="progress-percent">${compliance.carbs}%</span>
                    </div>
                    <div class="progress-item">
                        <div class="progress-header">
                            <span class="progress-label">지방</span>
                            <span class="progress-values">${todayTotal.fat} / ${goals.dailyFat}g</span>
                        </div>
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${compliance.fat}%; background: ${compliance.fat >= 80 ? '#4CAF50' : '#FF9800'};"></div>
                        </div>
                        <span class="progress-percent">${compliance.fat}%</span>
                    </div>
                    <div class="progress-item">
                        <div class="progress-header">
                            <span class="progress-label">섬유질</span>
                            <span class="progress-values">${todayTotal.fiber} / ${goals.dailyFiber}g</span>
                        </div>
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${compliance.fiber}%; background: ${compliance.fiber >= 80 ? '#4CAF50' : '#FF9800'};"></div>
                        </div>
                        <span class="progress-percent">${compliance.fiber}%</span>
                    </div>
                </div>
            </div>
        `;

        this.drawNutritionChart(todayTotal, goals);
    }

    drawNutritionChart(todayTotal, goals) {
        const canvas = document.getElementById('nutrition-chart');
        const ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        const nutrients = [
            { name: '칼로리', current: todayTotal.calories, goal: goals.dailyCalories, unit: 'kcal', color: '#2196F3' },
            { name: '단백질', current: todayTotal.protein, goal: goals.dailyProtein, unit: 'g', color: '#4CAF50' },
            { name: '탄수화물', current: todayTotal.carbs, goal: goals.dailyCarbs, unit: 'g', color: '#FF9800' },
            { name: '지방', current: todayTotal.fat, goal: goals.dailyFat, unit: 'g', color: '#F44336' },
            { name: '섬유질', current: todayTotal.fiber, goal: goals.dailyFiber, unit: 'g', color: '#9C27B0' }
        ];

        const width = canvas.width;
        const height = canvas.height;
        const padding = 60;
        const chartWidth = width - padding * 2;
        const chartHeight = height - padding * 2 - 40;
        const barWidth = chartWidth / nutrients.length;
        const maxValue = Math.max(...nutrients.map(n => Math.max(n.current, n.goal)));

        ctx.fillStyle = '#333';
        ctx.font = 'bold 16px Arial';
        ctx.textAlign = 'center';
        ctx.fillText('오늘의 영양소 섭취 현황', width / 2, 30);

        nutrients.forEach((nutrient, index) => {
            const x = padding + index * barWidth;
            const goalHeight = (nutrient.goal / maxValue) * chartHeight;
            const currentHeight = (nutrient.current / maxValue) * chartHeight;

            ctx.fillStyle = '#e0e0e0';
            ctx.fillRect(x + barWidth * 0.1, padding + chartHeight - goalHeight, barWidth * 0.8, goalHeight);

            ctx.fillStyle = nutrient.color;
            ctx.fillRect(x + barWidth * 0.1, padding + chartHeight - currentHeight, barWidth * 0.8, currentHeight);

            ctx.fillStyle = '#333';
            ctx.font = '12px Arial';
            ctx.textAlign = 'center';
            ctx.fillText(nutrient.name, x + barWidth / 2, height - 20);
            ctx.fillText(`${nutrient.current}${nutrient.unit}`, x + barWidth / 2, padding + chartHeight - currentHeight - 5);
        });
    }

    calculateMealTotal() {

    }

    initializeExerciseDatabase() {
        return {
            'RUNNING': {
                name: '러닝',
                category: 'CARDIO',
                caloriesPerMinute: 10,
                description: '심폐 지구력을 향상시키는 유산소 운동',
                instructions: [
                    '준비운동: 5분 가벼운 걷기',
                    '본 운동: 목표 심박수 유지하며 달리기',
                    '마무리: 5분 걷기 및 스트레칭'
                ],
                precautions: [
                    '무릎이나 발목에 부상이 있다면 주의',
                    '적절한 러닝화 착용 필수',
                    '충분한 수분 섭취',
                    '과도한 운동은 피하기'
                ],
                videoUrl: 'https://www.youtube.com/watch?v=example1',
                targetHeartRate: { min: 120, max: 160 }
            },
            'WALKING': {
                name: '걷기',
                category: 'CARDIO',
                caloriesPerMinute: 4,
                description: '부상 위험이 낮은 가벼운 유산소 운동',
                instructions: [
                    '바른 자세로 걷기',
                    '팔을 자연스럽게 흔들기',
                    '충분한 보폭 유지'
                ],
                precautions: [
                    '편안한 신발 착용',
                    '충분한 수분 섭취'
                ],
                videoUrl: 'https://www.youtube.com/watch?v=example2',
                targetHeartRate: { min: 90, max: 120 }
            },
            'CYCLING': {
                name: '자전거',
                category: 'CARDIO',
                caloriesPerMinute: 8,
                description: '하체 근력과 심폐 지구력 향상',
                instructions: [
                    '안전장비 착용 (헬멧 필수)',
                    '적절한 자세 유지',
                    '일정한 페이스 유지'
                ],
                precautions: [
                    '교통 안전 주의',
                    '헬멧 착용 필수',
                    '무릎 부상 시 주의'
                ],
                videoUrl: 'https://www.youtube.com/watch?v=example3',
                targetHeartRate: { min: 110, max: 150 }
            },
            'SWIMMING': {
                name: '수영',
                category: 'CARDIO',
                caloriesPerMinute: 12,
                description: '전신 운동으로 관절 부담이 적음',
                instructions: [
                    '준비운동: 5분 가벼운 스트레칭',
                    '본 운동: 다양한 영법으로 수영',
                    '마무리: 가벼운 스트레칭'
                ],
                precautions: [
                    '수영 전 충분한 준비운동',
                    '물에 들어가기 전 체온 유지',
                    '수영 후 귀 물 빼기'
                ],
                videoUrl: 'https://www.youtube.com/watch?v=example1',
                targetHeartRate: { min: 120, max: 160 }
            },
            'WEIGHT_TRAINING': {
                name: '근력 운동',
                category: 'STRENGTH',
                caloriesPerMinute: 6,
                description: '근육량 증가와 기초 대사율 향상',
                instructions: [
                    '준비운동: 10분 가벼운 유산소',
                    '본 운동: 3세트 x 10-12회',
                    '세트 간 1-2분 휴식',
                    '마무리: 스트레칭'
                ],
                precautions: [
                    '올바른 자세 유지',
                    '과도한 무게 피하기',
                    '충분한 휴식 시간',
                    '허리 부상 시 주의'
                ],
                videoUrl: 'https://www.youtube.com/watch?v=example5',
                targetHeartRate: { min: 100, max: 140 }
            },
            'YOGA': {
                name: '요가',
                category: 'FLEXIBILITY',
                caloriesPerMinute: 3,
                description: '유연성 향상과 스트레스 완화',
                instructions: [
                    '명상과 호흡으로 시작',
                    '각 자세를 천천히 수행',
                    '자신의 한계 내에서 수행',
                    '마무리: 쉬바사나(휴식 자세)'
                ],
                precautions: [
                    '무리한 자세 피하기',
                    '호흡에 집중',
                    '목과 척추 주의'
                ],
                videoUrl: 'https://www.youtube.com/watch?v=example6',
                targetHeartRate: { min: 80, max: 110 }
            },
            'PILATES': {
                name: '필라테스',
                category: 'FLEXIBILITY',
                caloriesPerMinute: 4,
                description: '코어 근육 강화와 자세 개선',
                instructions: [
                    '준비: 호흡법 숙지',
                    '본 운동: 코어 중심 운동',
                    '정확한 자세 유지',
                    '마무리: 스트레칭'
                ],
                precautions: [
                    '허리 통증 시 주의',
                    '올바른 호흡법 유지',
                    '무리한 동작 피하기'
                ],
                videoUrl: 'https://www.youtube.com/watch?v=example2',
                targetHeartRate: { min: 90, max: 120 }
            },
            'STRETCHING': {
                name: '스트레칭',
                category: 'FLEXIBILITY',
                caloriesPerMinute: 2,
                description: '근육 이완과 유연성 향상',
                instructions: [
                    '천천히 스트레칭 시작',
                    '통증이 느껴지면 중단',
                    '각 동작 20-30초 유지',
                    '호흡은 자연스럽게'
                ],
                precautions: [
                    '통증이 느껴지면 중단',
                    '과도한 스트레칭 피하기',
                    '운동 후 실시 권장'
                ],
                videoUrl: 'https://www.youtube.com/watch?v=example8',
                targetHeartRate: { min: 70, max: 100 }
            },
            'HIIT': {
                name: 'HIIT',
                category: 'CARDIO',
                caloriesPerMinute: 15,
                description: '고강도 인터벌 트레이닝으로 시간 대비 효과 높음',
                instructions: [
                    '준비운동: 10분 가벼운 유산소',
                    '본 운동: 30초 고강도 + 30초 휴식 반복',
                    '총 20분 수행',
                    '마무리: 충분한 스트레칭'
                ],
                precautions: [
                    '초보자는 주의',
                    '심장 질환자 금지',
                    '충분한 준비운동 필수',
                    '과도한 운동 피하기'
                ],
                videoUrl: 'https://www.youtube.com/watch?v=example9',
                targetHeartRate: { min: 150, max: 180 }
            }
        };
    }

    generateExerciseProgram() {
        const goal = document.getElementById('exercise-goal').value;
        const exerciseType = document.getElementById('exercise-type').value;
        const weeklyFrequency = parseInt(document.getElementById('weekly-frequency').value);
        const duration = parseInt(document.getElementById('program-duration').value);
        const fitnessLevel = document.getElementById('fitness-level').value;
        const healthConditions = Array.from(document.querySelectorAll('input[name="exercise-health-conditions"]:checked'))
            .map(cb => cb.value);

        if (!goal || !exerciseType || !weeklyFrequency || !duration || !fitnessLevel) {
            this.showToast('필수 정보를 모두 입력해주세요.', 'error');
            return;
        }

        const startDate = new Date();
        const weeks = [];
        const dayNames = ['일', '월', '화', '수', '목', '금', '토'];

        for (let week = 1; week <= duration; week++) {
            const weekPlan = {
                week: week,
                startDate: new Date(startDate.getTime() + (week - 1) * 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
                exercises: []
            };

            const exerciseDays = this.selectExerciseDays(weeklyFrequency);
            exerciseDays.forEach((dayIndex, index) => {
                const exercise = this.generateExerciseForDay(
                    goal,
                    exerciseType,
                    fitnessLevel,
                    healthConditions,
                    week,
                    index
                );
                weekPlan.exercises.push({
                    day: dayIndex,
                    dayName: dayNames[dayIndex],
                    ...exercise
                });
            });

            weeks.push(weekPlan);
        }

        this.exerciseProgram = {
            programSeq: Date.now(),
            userId: this.currentUser.userId,
            goal: goal,
            exerciseType: exerciseType,
            weeklyFrequency: weeklyFrequency,
            duration: duration,
            fitnessLevel: fitnessLevel,
            healthConditions: healthConditions,
            startDate: weeks[0].startDate,
            endDate: weeks[weeks.length - 1].startDate,
            weeks: weeks
        };

        this.saveToStorage();
        this.hideExerciseProgramModal();
        this.updateExerciseProgram();
        this.showToast('운동 프로그램이 생성되었습니다!', 'success');
    }

    selectExerciseDays(frequency) {
        const patterns = {
            2: [[1, 4], [2, 5], [0, 3]], 
            3: [[1, 3, 5], [0, 2, 4]], 
            4: [[1, 2, 4, 5], [0, 1, 3, 5]], 
            5: [[1, 2, 3, 4, 5], [0, 1, 2, 4, 5]] 
        };
        const options = patterns[frequency] || patterns[3];
        return options[Math.floor(Math.random() * options.length)];
    }

    generateExerciseForDay(goal, exerciseType, fitnessLevel, healthConditions, week, dayIndex) {
        const availableExercises = this.getAvailableExercises(exerciseType, healthConditions);
        const selectedExercise = availableExercises[dayIndex % availableExercises.length];
        const exerciseData = this.exerciseDatabase[selectedExercise];

        const duration = this.calculateExerciseDuration(fitnessLevel, exerciseData.category, week);
        const intensity = this.calculateIntensity(fitnessLevel, week);
        const sets = exerciseData.category === 'STRENGTH' ? this.calculateSets(fitnessLevel, week) : null;
        const reps = exerciseData.category === 'STRENGTH' ? this.calculateReps(fitnessLevel, week) : null;

        return {
            exerciseCode: selectedExercise,
            exerciseName: exerciseData.name,
            duration: duration,
            intensity: intensity,
            sets: sets,
            reps: reps,
            targetHeartRate: exerciseData.targetHeartRate,
            calories: Math.round(exerciseData.caloriesPerMinute * duration),
            notes: this.generateExerciseNotes(fitnessLevel, week)
        };
    }

    getAvailableExercises(exerciseType, healthConditions) {
        let exercises = Object.keys(this.exerciseDatabase);

        if (exerciseType === 'CARDIO') {
            exercises = exercises.filter(e => this.exerciseDatabase[e].category === 'CARDIO');
        } else if (exerciseType === 'STRENGTH') {
            exercises = exercises.filter(e => this.exerciseDatabase[e].category === 'STRENGTH');
        } else if (exerciseType === 'FLEXIBILITY') {
            exercises = exercises.filter(e => this.exerciseDatabase[e].category === 'FLEXIBILITY');
        }

        if (healthConditions.includes('KNEE_INJURY')) {
            exercises = exercises.filter(e => !['RUNNING', 'HIIT'].includes(e));
        }
        if (healthConditions.includes('BACK_PAIN')) {
            exercises = exercises.filter(e => !['WEIGHT_TRAINING', 'HIIT'].includes(e));
        }
        if (healthConditions.includes('HYPERTENSION')) {
            exercises = exercises.filter(e => e !== 'HIIT');
        }

        return exercises.length > 0 ? exercises : ['WALKING', 'STRETCHING'];
    }

    calculateExerciseDuration(fitnessLevel, category, week) {
        const baseDurations = {
            'BEGINNER': { 'CARDIO': 20, 'STRENGTH': 30, 'FLEXIBILITY': 20 },
            'INTERMEDIATE': { 'CARDIO': 30, 'STRENGTH': 45, 'FLEXIBILITY': 30 },
            'ADVANCED': { 'CARDIO': 45, 'STRENGTH': 60, 'FLEXIBILITY': 40 }
        };

        const base = baseDurations[fitnessLevel][category] || 30;
        const progression = Math.min(week * 2, 20);
        return base + progression;
    }

    calculateIntensity(fitnessLevel, week) {
        if (fitnessLevel === 'BEGINNER') {
            return week <= 4 ? 'LOW' : week <= 8 ? 'MODERATE' : 'HIGH';
        } else if (fitnessLevel === 'INTERMEDIATE') {
            return week <= 2 ? 'MODERATE' : week <= 6 ? 'HIGH' : 'VERY_HIGH';
        } else {
            return week <= 2 ? 'HIGH' : 'VERY_HIGH';
        }
    }

    calculateSets(fitnessLevel, week) {
        if (fitnessLevel === 'BEGINNER') {
            return week <= 4 ? 2 : 3;
        } else if (fitnessLevel === 'INTERMEDIATE') {
            return week <= 2 ? 3 : 4;
        } else {
            return 4;
        }
    }

    calculateReps(fitnessLevel, week) {
        if (fitnessLevel === 'BEGINNER') {
            return week <= 4 ? 8 : 10;
        } else if (fitnessLevel === 'INTERMEDIATE') {
            return week <= 2 ? 10 : 12;
        } else {
            return 12;
        }
    }

    generateExerciseNotes(fitnessLevel, week) {
        if (week <= 2) {
            return '운동 초기 단계입니다. 무리하지 말고 천천히 시작하세요.';
        } else if (week <= 6) {
            return '운동에 익숙해지는 단계입니다. 점진적으로 강도를 높이세요.';
        } else {
            return '운동이 습관화되었습니다. 꾸준히 유지하세요.';
        }
    }

    updateExerciseScreen() {
        this.updateExerciseProgram();
        this.updateExerciseRecords();
        this.updateExerciseGuide();
    }

    switchExerciseTab(tabName) {
        this.exerciseCurrentTab = tabName;
        document.querySelectorAll('#exercise-screen .tab-button').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelectorAll('#exercise-screen .tab-content').forEach(content => {
            content.classList.remove('active');
        });
        document.querySelector(`#exercise-screen [data-tab="${tabName}"]`).classList.add('active');
        document.getElementById(`tab-${tabName}`).classList.add('active');

        if (tabName === 'exercise-guide') {
            this.updateExerciseGuide();
        } else if (tabName === 'exercise-records') {
            this.updateExerciseRecords();
        } else if (tabName === 'exercise-program') {
            this.updateExerciseProgram();
        }
    }

    updateExerciseProgram() {
        const summaryContainer = document.getElementById('program-summary');
        const scheduleContainer = document.getElementById('program-schedule');

        if (!this.exerciseProgram) {
            summaryContainer.innerHTML = `
                <div class="no-data-message">
                    <i class="fas fa-dumbbell"></i>
                    <p>운동 프로그램이 없습니다.</p>
                    <p>맞춤 운동 프로그램을 생성해보세요!</p>
                </div>
            `;
            scheduleContainer.innerHTML = '';
            return;
        }

        const goalNames = {
            'WEIGHT_LOSS': '체중 감량',
            'MUSCLE_GAIN': '근육 증가',
            'STRENGTH': '체력 향상',
            'FLEXIBILITY': '유연성 향상',
            'GENERAL_FITNESS': '전반적 건강'
        };

        const typeNames = {
            'CARDIO': '유산소',
            'STRENGTH': '근력',
            'FLEXIBILITY': '유연성',
            'MIXED': '혼합'
        };

        summaryContainer.innerHTML = `
            <div class="program-summary-card">
                <h3>운동 프로그램 요약</h3>
                <div class="summary-grid">
                    <div class="summary-item">
                        <span class="summary-label">목표</span>
                        <span class="summary-value">${goalNames[this.exerciseProgram.goal]}</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">운동 유형</span>
                        <span class="summary-value">${typeNames[this.exerciseProgram.exerciseType]}</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">주당 횟수</span>
                        <span class="summary-value">${this.exerciseProgram.weeklyFrequency}회</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">기간</span>
                        <span class="summary-value">${this.exerciseProgram.duration}주</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">수준</span>
                        <span class="summary-value">${this.exerciseProgram.fitnessLevel === 'BEGINNER' ? '초급' : this.exerciseProgram.fitnessLevel === 'INTERMEDIATE' ? '중급' : '고급'}</span>
                    </div>
                    <div class="summary-item">
                        <span class="summary-label">기간</span>
                        <span class="summary-value">${this.exerciseProgram.startDate} ~ ${this.exerciseProgram.endDate}</span>
                    </div>
                </div>
            </div>
        `;

        scheduleContainer.innerHTML = '';
        this.exerciseProgram.weeks.forEach(week => {
            const weekCard = document.createElement('div');
            weekCard.className = 'week-card';

            weekCard.innerHTML = `
                <div class="week-header">
                    <h4>${week.week}주차 - ${week.startDate}</h4>
                </div>
                <div class="week-exercises">
                    ${week.exercises.map(ex => {
                        const intensityNames = {
                            'LOW': '낮음',
                            'MODERATE': '보통',
                            'HIGH': '높음',
                            'VERY_HIGH': '매우 높음'
                        };
                        return `
                            <div class="exercise-schedule-item">
                                <div class="schedule-header">
                                    <span class="schedule-day">${ex.dayName}요일</span>
                                    <span class="schedule-exercise">${ex.exerciseName}</span>
                                </div>
                                <div class="schedule-details">
                                    <span><i class="fas fa-clock"></i> ${ex.duration}분</span>
                                    <span><i class="fas fa-fire"></i> ${ex.calories} kcal</span>
                                    <span><i class="fas fa-heartbeat"></i> 심박수: ${ex.targetHeartRate.min}-${ex.targetHeartRate.max} bpm</span>
                                    ${ex.sets ? `<span><i class="fas fa-dumbbell"></i> ${ex.sets}세트 x ${ex.reps}회</span>` : ''}
                                    <span class="intensity-badge intensity-${ex.intensity.toLowerCase()}">${intensityNames[ex.intensity]}</span>
                                </div>
                                ${ex.notes ? `<div class="schedule-notes">${ex.notes}</div>` : ''}
                                <button class="guide-button" onclick="window.healthcareApp.showExerciseGuide('${ex.exerciseCode}')">
                                    <i class="fas fa-play-circle"></i> 운동 가이드 보기
                                </button>
                            </div>
                        `;
                    }).join('')}
                </div>
            `;
            scheduleContainer.appendChild(weekCard);
        });
    }

    showExerciseProgramModal() {
        document.getElementById('exercise-program-modal').classList.remove('hidden');
        document.getElementById('exercise-goal').value = '';
        document.getElementById('exercise-type').value = '';
        document.getElementById('weekly-frequency').value = '';
        document.getElementById('program-duration').value = '12';
        document.getElementById('fitness-level').value = '';
        document.querySelectorAll('input[name="exercise-health-conditions"]').forEach(cb => cb.checked = false);
    }

    hideExerciseProgramModal() {
        document.getElementById('exercise-program-modal').classList.add('hidden');
    }

    showExerciseRecordModal() {
        document.getElementById('exercise-record-modal').classList.remove('hidden');
        document.getElementById('exercise-date').value = new Date().toISOString().split('T')[0];
        document.getElementById('exercise-code').value = '';
        document.getElementById('exercise-duration').value = '';
        document.getElementById('heart-rate-avg').value = '';
        document.getElementById('heart-rate-max').value = '';
        document.getElementById('exercise-distance').value = '';
        document.getElementById('exercise-intensity').value = '';
        document.getElementById('exercise-notes').value = '';
    }

    hideExerciseRecordModal() {
        document.getElementById('exercise-record-modal').classList.add('hidden');
    }

    submitExerciseRecord() {
        const recordDate = document.getElementById('exercise-date').value;
        const exerciseCode = document.getElementById('exercise-code').value;
        const duration = parseInt(document.getElementById('exercise-duration').value);
        const heartRateAvg = document.getElementById('heart-rate-avg').value ? parseInt(document.getElementById('heart-rate-avg').value) : null;
        const heartRateMax = document.getElementById('heart-rate-max').value ? parseInt(document.getElementById('heart-rate-max').value) : null;
        const distance = document.getElementById('exercise-distance').value ? parseFloat(document.getElementById('exercise-distance').value) : null;
        const intensity = document.getElementById('exercise-intensity').value || null;
        const notes = document.getElementById('exercise-notes').value || null;

        if (!recordDate || !exerciseCode || !duration) {
            this.showToast('필수 정보를 입력해주세요.', 'error');
            return;
        }

        const exerciseData = this.exerciseDatabase[exerciseCode];
        const caloriesBurned = exerciseData ? Math.round(exerciseData.caloriesPerMinute * duration) : Math.round(5 * duration);

        const exerciseRecord = {
            recordSeq: Date.now(),
            userId: this.currentUser.userId,
            recordDate: recordDate,
            exerciseCode: exerciseCode,
            exerciseName: exerciseData ? exerciseData.name : exerciseCode,
            duration: duration,
            caloriesBurned: caloriesBurned,
            heartRateAvg: heartRateAvg,
            heartRateMax: heartRateMax,
            distance: distance,
            intensity: intensity,
            notes: notes
        };

        this.exerciseRecords.unshift(exerciseRecord);
        this.exerciseRecords.sort((a, b) => {
            const dateCompare = new Date(b.recordDate) - new Date(a.recordDate);
            if (dateCompare !== 0) return dateCompare;
            return b.recordSeq - a.recordSeq;
        });

        this.saveToStorage();
        this.hideExerciseRecordModal();
        this.updateExerciseRecords();
        this.showToast('운동 기록이 저장되었습니다.', 'success');
    }

    updateExerciseRecords() {
        const container = document.getElementById('exercise-records-list');

        if (this.exerciseRecords.length === 0) {
            container.innerHTML = `
                <div class="no-data-message">
                    <i class="fas fa-dumbbell"></i>
                    <p>운동 기록이 없습니다.</p>
                    <p>운동을 기록해보세요!</p>
                </div>
            `;
            return;
        }

        container.innerHTML = '';
        const groupedByDate = {};

        this.exerciseRecords.forEach(record => {
            if (!groupedByDate[record.recordDate]) {
                groupedByDate[record.recordDate] = [];
            }
            groupedByDate[record.recordDate].push(record);
        });

        Object.keys(groupedByDate).sort((a, b) => new Date(b) - new Date(a)).forEach(date => {
            const dateCard = document.createElement('div');
            dateCard.className = 'exercise-date-card';

            const dayRecords = groupedByDate[date];
            const dayTotal = dayRecords.reduce((sum, r) => sum + r.caloriesBurned, 0);
            const dayDuration = dayRecords.reduce((sum, r) => sum + r.duration, 0);

            dateCard.innerHTML = `
                <div class="date-header">
                    <h4>${date}</h4>
                    <div class="date-totals">
                        <span class="date-total">총 ${dayTotal} kcal</span>
                        <span class="date-duration">${dayDuration}분</span>
                    </div>
                </div>
                <div class="date-exercises">
                    ${dayRecords.map(record => {
                        const intensityNames = {
                            'LOW': '낮음',
                            'MODERATE': '보통',
                            'HIGH': '높음',
                            'VERY_HIGH': '매우 높음'
                        };
                        return `
                            <div class="exercise-record-card">
                                <div class="record-header">
                                    <span class="exercise-name">${record.exerciseName}</span>
                                    <span class="record-calories">${record.caloriesBurned} kcal</span>
                                </div>
                                <div class="record-details">
                                    <span><i class="fas fa-clock"></i> ${record.duration}분</span>
                                    ${record.distance ? `<span><i class="fas fa-route"></i> ${record.distance}km</span>` : ''}
                                    ${record.heartRateAvg ? `<span><i class="fas fa-heartbeat"></i> 평균 ${record.heartRateAvg} bpm</span>` : ''}
                                    ${record.heartRateMax ? `<span><i class="fas fa-heart"></i> 최대 ${record.heartRateMax} bpm</span>` : ''}
                                    ${record.intensity ? `<span class="intensity-badge intensity-${record.intensity.toLowerCase()}">${intensityNames[record.intensity]}</span>` : ''}
                                </div>
                                ${record.notes ? `<div class="record-notes">${record.notes}</div>` : ''}
                            </div>
                        `;
                    }).join('')}
                </div>
            `;
            container.appendChild(dateCard);
        });
    }

    updateExerciseGuide() {
        const container = document.getElementById('guide-categories');

        const categories = {
            'CARDIO': { name: '유산소 운동', icon: 'fa-running', exercises: [] },
            'STRENGTH': { name: '근력 운동', icon: 'fa-dumbbell', exercises: [] },
            'FLEXIBILITY': { name: '유연성 운동', icon: 'fa-leaf', exercises: [] }
        };

        Object.keys(this.exerciseDatabase).forEach(code => {
            const exercise = this.exerciseDatabase[code];
            categories[exercise.category].exercises.push({ code, ...exercise });
        });

        container.innerHTML = Object.keys(categories).map(category => `
            <div class="guide-category-card">
                <div class="category-header">
                    <i class="fas ${categories[category].icon}"></i>
                    <h3>${categories[category].name}</h3>
                </div>
                <div class="category-exercises">
                    ${categories[category].exercises.map(ex => `
                        <div class="guide-exercise-item" onclick="window.healthcareApp.showExerciseGuide('${ex.code}')">
                            <div class="exercise-guide-info">
                                <h4>${ex.name}</h4>
                                <p>${ex.description}</p>
                            </div>
                            <button class="guide-button">
                                <i class="fas fa-play-circle"></i> 가이드 보기
                            </button>
                        </div>
                    `).join('')}
                </div>
            </div>
        `).join('');
    }

    showExerciseGuide(exerciseCode) {
        const exercise = this.exerciseDatabase[exerciseCode];
        if (!exercise) return;

        const intensityNames = {
            'LOW': '낮음',
            'MODERATE': '보통',
            'HIGH': '높음',
            'VERY_HIGH': '매우 높음'
        };

        document.getElementById('guide-modal-title').textContent = `${exercise.name} 가이드`;
        document.getElementById('exercise-guide-content').innerHTML = `
            <div class="exercise-guide-detail">
                <div class="guide-description">
                    <h4>운동 설명</h4>
                    <p>${exercise.description}</p>
                </div>
                <div class="guide-video">
                    <h4>운동 동영상</h4>
                    <div class="video-container">
                        <iframe width="100%" height="315" src="${exercise.videoUrl}" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                    </div>
                </div>
                <div class="guide-instructions">
                    <h4>운동 방법</h4>
                    <ol>
                        ${exercise.instructions.map(inst => `<li>${inst}</li>`).join('')}
                    </ol>
                </div>
                <div class="guide-precautions">
                    <h4>주의사항</h4>
                    <ul>
                        ${exercise.precautions.map(prec => `<li>${prec}</li>`).join('')}
                    </ul>
                </div>
                <div class="guide-info">
                    <div class="info-item">
                        <span class="info-label">분당 칼로리 소모</span>
                        <span class="info-value">${exercise.caloriesPerMinute} kcal/분</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">목표 심박수</span>
                        <span class="info-value">${exercise.targetHeartRate.min} - ${exercise.targetHeartRate.max} bpm</span>
                    </div>
                </div>
            </div>
        `;
        document.getElementById('exercise-guide-modal').classList.remove('hidden');
    }

    hideExerciseGuideModal() {
        document.getElementById('exercise-guide-modal').classList.add('hidden');
    }

    initializeContentDatabase() {
        return [
            {
                contentId: 1,
                title: '고혈압 관리에 좋은 식단 10가지',
                category: 'HEALTH_INFO',
                categoryName: '건강 정보',
                tags: ['고혈압', '식단', '건강'],
                description: '고혈압을 관리하기 위한 효과적인 식단 가이드입니다.',
                content: `
                    <h3>고혈압 관리 식단 가이드</h3>
                    <p>고혈압은 심장 질환과 뇌졸중의 주요 위험 요인입니다. 올바른 식단으로 혈압을 효과적으로 관리할 수 있습니다.</p>
                    <h4>추천 음식</h4>
                    <ul>
                        <li>바나나: 칼륨이 풍부하여 나트륨 배출에 도움</li>
                        <li>시금치: 마그네슘과 칼륨이 풍부</li>
                        <li>오트밀: 섬유질이 풍부하여 콜레스테롤 감소</li>
                        <li>연어: 오메가-3 지방산이 심혈관 건강에 좋음</li>
                        <li>요구르트: 칼슘이 풍부하고 저염</li>
                    </ul>
                    <h4>피해야 할 음식</h4>
                    <ul>
                        <li>가공 식품: 나트륨 함량이 높음</li>
                        <li>인스턴트 식품: 염분이 과다</li>
                        <li>술과 카페인: 과도한 섭취는 혈압 상승</li>
                    </ul>
                `,
                viewCount: 1250,
                likeCount: 89,
                healthConditions: ['HYPERTENSION']
            },
            {
                contentId: 2,
                title: '당뇨 예방을 위한 운동 가이드',
                category: 'EXERCISE_GUIDE',
                categoryName: '운동 가이드',
                tags: ['당뇨', '운동', '예방'],
                description: '당뇨 예방과 관리에 효과적인 운동 방법을 안내합니다.',
                content: `
                    <h3>당뇨 예방 운동 가이드</h3>
                    <p>규칙적인 운동은 혈당 조절과 인슐린 감수성 향상에 도움이 됩니다.</p>
                    <h4>추천 운동</h4>
                    <ul>
                        <li>걷기: 하루 30분 이상, 주 5회</li>
                        <li>수영: 전신 운동으로 혈당 조절에 효과적</li>
                        <li>자전거 타기: 관절 부담이 적은 유산소 운동</li>
                        <li>근력 운동: 근육량 증가로 인슐린 감수성 향상</li>
                    </ul>
                    <h4>주의사항</h4>
                    <ul>
                        <li>운동 전 혈당 측정 필수</li>
                        <li>저혈당 증상 시 즉시 중단</li>
                        <li>충분한 수분 섭취</li>
                    </ul>
                `,
                viewCount: 980,
                likeCount: 67,
                healthConditions: ['DIABETES']
            },
            {
                contentId: 3,
                title: '저염식 레시피 모음',
                category: 'RECIPE',
                categoryName: '레시피',
                tags: ['저염식', '레시피', '건강'],
                description: '염분을 줄이면서도 맛있는 건강 레시피를 소개합니다.',
                content: `
                    <h3>저염식 레시피</h3>
                    <h4>1. 시금치 두부 볶음</h4>
                    <p>재료: 시금치 200g, 두부 1모, 마늘, 참기름</p>
                    <p>조리법: 시금치를 데친 후 두부와 함께 볶고, 소금 대신 참기름으로 간을 맞춥니다.</p>
                    <h4>2. 연어 샐러드</h4>
                    <p>재료: 연어 150g, 양상추, 토마토, 올리브오일</p>
                    <p>조리법: 신선한 연어를 구워 샐러드와 함께 올리고, 올리브오일 드레싱을 뿌립니다.</p>
                `,
                viewCount: 2100,
                likeCount: 145,
                healthConditions: ['HYPERTENSION', 'HIGH_CHOLESTEROL']
            },
            {
                contentId: 4,
                title: '심혈관 건강을 위한 생활 습관',
                category: 'HEALTH_INFO',
                categoryName: '건강 정보',
                tags: ['심혈관', '건강', '생활습관'],
                description: '심혈관 건강을 지키는 일상 생활 습관을 알아봅니다.',
                content: `
                    <h3>심혈관 건강 생활 습관</h3>
                    <h4>1. 규칙적인 운동</h4>
                    <p>주 3회 이상, 30분 이상의 유산소 운동을 권장합니다.</p>
                    <h4>2. 건강한 식단</h4>
                    <p>포화 지방과 트랜스 지방을 줄이고, 오메가-3 지방산을 섭취하세요.</p>
                    <h4>3. 금연</h4>
                    <p>흡연은 심혈관 질환의 주요 위험 요인입니다.</p>
                    <h4>4. 스트레스 관리</h4>
                    <p>명상, 요가, 취미 활동으로 스트레스를 관리하세요.</p>
                `,
                viewCount: 1560,
                likeCount: 112,
                healthConditions: ['HIGH_CHOLESTEROL', 'HYPERTENSION']
            },
            {
                contentId: 5,
                title: '체중 감량을 위한 효과적인 운동법',
                category: 'EXERCISE_GUIDE',
                categoryName: '운동 가이드',
                tags: ['체중감량', '운동', '다이어트'],
                description: '효과적인 체중 감량을 위한 운동 방법을 소개합니다.',
                content: `
                    <h3>체중 감량 운동법</h3>
                    <h4>1. 유산소 운동</h4>
                    <p>러닝, 자전거, 수영 등 주 5회, 30-60분씩 실시</p>
                    <h4>2. 근력 운동</h4>
                    <p>근육량 증가로 기초 대사율 향상, 주 2-3회 실시</p>
                    <h4>3. HIIT</h4>
                    <p>고강도 인터벌 트레이닝으로 시간 대비 효과 높음</p>
                `,
                viewCount: 3200,
                likeCount: 234,
                healthConditions: []
            },
            {
                contentId: 6,
                title: '건강한 아침 식단 레시피',
                category: 'RECIPE',
                categoryName: '레시피',
                tags: ['아침식사', '레시피', '건강'],
                description: '하루를 시작하는 건강한 아침 식단을 소개합니다.',
                content: `
                    <h3>건강한 아침 식단</h3>
                    <h4>1. 오트밀과 베리</h4>
                    <p>오트밀에 블루베리, 딸기를 올려 섬유질과 항산화 물질을 섭취</p>
                    <h4>2. 계란과 토스트</h4>
                    <p>전곡 빵에 계란후라이와 아보카도를 올린 영양 만점 식사</p>
                `,
                viewCount: 1890,
                likeCount: 156,
                healthConditions: []
            }
        ];
    }

    initializeServiceDatabase() {
        return [
            {
                serviceId: 1,
                name: '강남구 심장내과',
                type: 'HOSPITAL',
                typeName: '병원',
                category: 'CARDIOLOGY',
                categoryName: '심장내과',
                address: '서울시 강남구 테헤란로 123',
                phone: '02-1234-5678',
                distance: 2.0,
                rating: 4.5,
                reviewCount: 128,
                description: '심혈관 질환 전문 진료, 24시간 응급실 운영',
                services: ['심장 초음파', '심전도', '혈압 관리', '건강검진'],
                hours: '평일 09:00-18:00, 토요일 09:00-13:00',
                healthConditions: ['HYPERTENSION', 'HIGH_CHOLESTEROL']
            },
            {
                serviceId: 2,
                name: '강남구 약국',
                type: 'PHARMACY',
                typeName: '약국',
                category: 'GENERAL',
                categoryName: '일반',
                address: '서울시 강남구 역삼동 456',
                phone: '02-2345-6789',
                distance: 0.5,
                rating: 4.2,
                reviewCount: 89,
                description: '건강 상담 및 처방전 조제, 건강기능식품 판매',
                services: ['처방전 조제', '건강 상담', '혈압 측정', '혈당 측정'],
                hours: '평일 09:00-21:00, 토요일 09:00-18:00',
                healthConditions: []
            },
            {
                serviceId: 3,
                name: '강남구 헬스장',
                type: 'GYM',
                typeName: '헬스장',
                category: 'FITNESS',
                categoryName: '피트니스',
                address: '서울시 강남구 논현동 789',
                phone: '02-3456-7890',
                distance: 1.0,
                rating: 4.7,
                reviewCount: 256,
                description: '최신 운동 기구와 전문 트레이너 상주',
                services: ['개인 트레이닝', '그룹 클래스', '요가', '필라테스'],
                hours: '평일 06:00-23:00, 주말 08:00-22:00',
                healthConditions: []
            },
            {
                serviceId: 4,
                name: '강남구 내과',
                type: 'HOSPITAL',
                typeName: '병원',
                category: 'INTERNAL',
                categoryName: '내과',
                address: '서울시 강남구 선릉로 321',
                phone: '02-4567-8901',
                distance: 1.5,
                rating: 4.3,
                reviewCount: 95,
                description: '일반 내과 진료 및 건강검진',
                services: ['일반 진료', '건강검진', '예방접종', '건강 상담'],
                hours: '평일 09:00-18:00',
                healthConditions: []
            },
            {
                serviceId: 5,
                name: '강남구 한의원',
                type: 'HOSPITAL',
                typeName: '병원',
                category: 'KOREAN_MEDICINE',
                categoryName: '한의원',
                address: '서울시 강남구 봉은사로 654',
                phone: '02-5678-9012',
                distance: 2.5,
                rating: 4.6,
                reviewCount: 167,
                description: '체질 개선 및 만성 질환 치료',
                services: ['침술', '한약', '부항', '추나요법'],
                hours: '평일 09:00-19:00, 토요일 09:00-15:00',
                healthConditions: []
            },
            {
                serviceId: 6,
                name: '강남구 당뇨클리닉',
                type: 'HOSPITAL',
                typeName: '병원',
                category: 'ENDOCRINOLOGY',
                categoryName: '내분비과',
                address: '서울시 강남구 도곡동 987',
                phone: '02-6789-0123',
                distance: 3.0,
                rating: 4.8,
                reviewCount: 203,
                description: '당뇨 전문 진료 및 교육 프로그램',
                services: ['당뇨 진료', '혈당 관리', '영양 상담', '운동 처방'],
                hours: '평일 09:00-18:00',
                healthConditions: ['DIABETES']
            }
        ];
    }

    updateRecommendScreen() {
        this.updateContentRecommend();
        this.updateServiceRecommend();
        this.updatePersonalizedRecommend();
    }

    switchRecommendTab(tabName) {
        this.recommendCurrentTab = tabName;
        document.querySelectorAll('#recommend-screen .tab-button').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelectorAll('#recommend-screen .tab-content').forEach(content => {
            content.classList.remove('active');
        });
        document.querySelector(`#recommend-screen [data-tab="${tabName}"]`).classList.add('active');
        document.getElementById(`tab-${tabName}`).classList.add('active');

        if (tabName === 'content-recommend') {
            this.updateContentRecommend();
        } else if (tabName === 'service-recommend') {
            this.updateServiceRecommend();
        } else if (tabName === 'personalized') {
            this.updatePersonalizedRecommend();
        }
    }

    updateContentRecommend() {
        const filtersContainer = document.getElementById('content-filters');
        const listContainer = document.getElementById('content-list');

        filtersContainer.innerHTML = `
            <div class="filter-buttons">
                <button class="filter-btn active" data-category="ALL">전체</button>
                <button class="filter-btn" data-category="HEALTH_INFO">건강 정보</button>
                <button class="filter-btn" data-category="EXERCISE_GUIDE">운동 가이드</button>
                <button class="filter-btn" data-category="RECIPE">레시피</button>
            </div>
        `;

        filtersContainer.querySelectorAll('.filter-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                filtersContainer.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
                e.target.classList.add('active');
                const category = e.target.dataset.category;
                this.filterContentByCategory(category);
            });
        });

        this.filterContentByCategory('ALL');
    }

    filterContentByCategory(category) {
        const listContainer = document.getElementById('content-list');
        let filteredContents = this.contentDatabase;

        if (category !== 'ALL') {
            filteredContents = filteredContents.filter(c => c.category === category);
        }

        const userHealthConditions = this.getUserHealthConditions();
        const recommendedContents = this.recommendContent(filteredContents, userHealthConditions);

        listContainer.innerHTML = '';
        recommendedContents.forEach(content => {
            const card = document.createElement('div');
            card.className = 'content-card';
            card.innerHTML = `
                <div class="content-header">
                    <span class="content-category">${content.categoryName}</span>
                    <span class="content-stats">
                        <i class="fas fa-eye"></i> ${content.viewCount}
                        <i class="fas fa-heart"></i> ${content.likeCount}
                    </span>
                </div>
                <h4 class="content-title">${content.title}</h4>
                <p class="content-description">${content.description}</p>
                <div class="content-tags">
                    ${content.tags.map(tag => `<span class="tag">${tag}</span>`).join('')}
                </div>
                <button class="view-content-btn" onclick="window.healthcareApp.viewContent(${content.contentId})">
                    <i class="fas fa-arrow-right"></i> 자세히 보기
                </button>
            `;
            listContainer.appendChild(card);
        });
    }

    getUserHealthConditions() {
        const conditions = [];
        if (this.checkupData.length > 0) {
            const latestCheckup = this.checkupData[0];
            latestCheckup.items.forEach(item => {
                if (item.status === 'ABNORMAL') {
                    if (item.itemCode === 'BLOOD_PRESSURE' || item.itemName.includes('혈압')) {
                        conditions.push('HYPERTENSION');
                    }
                    if (item.itemCode === 'CHOLESTEROL' || item.itemName.includes('콜레스테롤')) {
                        conditions.push('HIGH_CHOLESTEROL');
                    }
                    if (item.itemCode === 'GLUCOSE' || item.itemName.includes('혈당')) {
                        conditions.push('DIABETES');
                    }
                }
            });
        }
        return [...new Set(conditions)];
    }

    recommendContent(contents, userHealthConditions) {
        return contents.map(content => {
            let score = 0;

            if (userHealthConditions.length > 0 && content.healthConditions.length > 0) {
                const matchCount = userHealthConditions.filter(c => content.healthConditions.includes(c)).length;
                score += matchCount * 10;
            }

            score += content.viewCount / 100;
            score += content.likeCount * 2;

            const viewed = this.contentViewHistory.find(h => h.contentId === content.contentId);
            if (viewed) {
                score -= 5;
            }

            return { ...content, score };
        }).sort((a, b) => b.score - a.score);
    }

    viewContent(contentId) {
        const content = this.contentDatabase.find(c => c.contentId === contentId);
        if (!content) return;

        this.contentViewHistory.push({
            contentId: contentId,
            viewedAt: new Date().toISOString()
        });
        this.saveToStorage();

        document.getElementById('content-detail-title').textContent = content.title;
        document.getElementById('content-detail-content').innerHTML = `
            <div class="content-detail-header">
                <span class="content-category">${content.categoryName}</span>
                <span class="content-stats">
                    <i class="fas fa-eye"></i> ${content.viewCount}
                    <i class="fas fa-heart"></i> ${content.likeCount}
                </span>
            </div>
            <div class="content-detail-body">
                ${content.content}
            </div>
            <div class="content-detail-tags">
                ${content.tags.map(tag => `<span class="tag">${tag}</span>`).join('')}
            </div>
        `;
        document.getElementById('content-detail-modal').classList.remove('hidden');
    }

    hideContentDetailModal() {
        document.getElementById('content-detail-modal').classList.add('hidden');
    }

    updateServiceRecommend() {
        const filtersContainer = document.getElementById('service-filters');
        const listContainer = document.getElementById('service-list');

        filtersContainer.innerHTML = `
            <div class="filter-buttons">
                <button class="filter-btn active" data-type="ALL">전체</button>
                <button class="filter-btn" data-type="HOSPITAL">병원</button>
                <button class="filter-btn" data-type="PHARMACY">약국</button>
                <button class="filter-btn" data-type="GYM">헬스장</button>
            </div>
        `;

        filtersContainer.querySelectorAll('.filter-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                filtersContainer.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
                e.target.classList.add('active');
                const type = e.target.dataset.type;
                this.filterServiceByType(type);
            });
        });

        this.filterServiceByType('ALL');
    }

    filterServiceByType(type) {
        const listContainer = document.getElementById('service-list');
        let filteredServices = this.serviceDatabase;

        if (type !== 'ALL') {
            filteredServices = filteredServices.filter(s => s.type === type);
        }

        const userHealthConditions = this.getUserHealthConditions();
        const recommendedServices = this.recommendService(filteredServices, userHealthConditions);

        listContainer.innerHTML = '';
        recommendedServices.forEach(service => {
            const card = document.createElement('div');
            card.className = 'service-card';
            card.innerHTML = `
                <div class="service-header">
                    <div class="service-info">
                        <h4 class="service-name">${service.name}</h4>
                        <span class="service-type">${service.typeName} - ${service.categoryName}</span>
                    </div>
                    <div class="service-rating">
                        <i class="fas fa-star"></i>
                        <span>${service.rating}</span>
                        <span class="review-count">(${service.reviewCount})</span>
                    </div>
                </div>
                <div class="service-details">
                    <p class="service-description">${service.description}</p>
                    <div class="service-meta">
                        <span><i class="fas fa-map-marker-alt"></i> ${service.address}</span>
                        <span><i class="fas fa-route"></i> ${service.distance}km</span>
                        <span><i class="fas fa-phone"></i> ${service.phone}</span>
                    </div>
                    <div class="service-hours">
                        <i class="fas fa-clock"></i> ${service.hours}
                    </div>
                </div>
                <button class="view-service-btn" onclick="window.healthcareApp.viewService(${service.serviceId})">
                    <i class="fas fa-info-circle"></i> 상세 정보
                </button>
            `;
            listContainer.appendChild(card);
        });
    }

    recommendService(services, userHealthConditions) {
        return services.map(service => {
            let score = service.rating * 10;

            if (userHealthConditions.length > 0 && service.healthConditions.length > 0) {
                const matchCount = userHealthConditions.filter(c => service.healthConditions.includes(c)).length;
                score += matchCount * 20;
            }

            score += service.reviewCount / 10;
            score -= service.distance * 2;

            return { ...service, score };
        }).sort((a, b) => b.score - a.score);
    }

    viewService(serviceId) {
        const service = this.serviceDatabase.find(s => s.serviceId === serviceId);
        if (!service) return;

        document.getElementById('service-detail-title').textContent = service.name;
        document.getElementById('service-detail-content').innerHTML = `
            <div class="service-detail-header">
                <div class="service-detail-info">
                    <span class="service-type">${service.typeName} - ${service.categoryName}</span>
                    <div class="service-rating">
                        <i class="fas fa-star"></i>
                        <span>${service.rating}</span>
                        <span class="review-count">(${service.reviewCount}개 리뷰)</span>
                    </div>
                </div>
            </div>
            <div class="service-detail-body">
                <div class="detail-section">
                    <h4>설명</h4>
                    <p>${service.description}</p>
                </div>
                <div class="detail-section">
                    <h4>주소</h4>
                    <p><i class="fas fa-map-marker-alt"></i> ${service.address}</p>
                    <p><i class="fas fa-route"></i> 거리: ${service.distance}km</p>
                </div>
                <div class="detail-section">
                    <h4>연락처</h4>
                    <p><i class="fas fa-phone"></i> ${service.phone}</p>
                </div>
                <div class="detail-section">
                    <h4>운영 시간</h4>
                    <p><i class="fas fa-clock"></i> ${service.hours}</p>
                </div>
                <div class="detail-section">
                    <h4>제공 서비스</h4>
                    <div class="service-list-items">
                        ${service.services.map(s => `<span class="service-item">${s}</span>`).join('')}
                    </div>
                </div>
            </div>
            ${service.type === 'HOSPITAL' ? `
            <div class="service-detail-actions">
                <button class="reservation-btn" onclick="window.healthcareApp.showServiceReservationModal(${service.serviceId})">
                    <i class="fas fa-calendar-check"></i> 예약하기
                </button>
            </div>
            ` : ''}
        `;
        document.getElementById('service-detail-modal').classList.remove('hidden');
    }

    hideServiceDetailModal() {
        document.getElementById('service-detail-modal').classList.add('hidden');
    }

    showServiceReservationModal(serviceId) {
        const service = this.serviceDatabase.find(s => s.serviceId === serviceId);
        if (!service || service.type !== 'HOSPITAL') {
            this.showToast('병원만 예약할 수 있습니다.', 'error');
            return;
        }

        document.getElementById('reservation-hospital-name').value = service.name;

        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(10, 0, 0, 0);
        document.getElementById('reservation-datetime').value = tomorrow.toISOString().slice(0, 16);

        if (this.currentUser && this.currentUser.phone) {
            document.getElementById('reservation-phone').value = this.currentUser.phone;
        }

        document.getElementById('service-reservation-modal').classList.remove('hidden');
        this.currentReservationServiceId = serviceId;
    }

    hideServiceReservationModal() {
        document.getElementById('service-reservation-modal').classList.add('hidden');
        const form = document.getElementById('service-reservation-form');
        if (form) {
            form.reset();
        }
        this.currentReservationServiceId = null;
    }

    submitServiceReservation() {
        const serviceId = this.currentReservationServiceId;
        if (!serviceId) {
            this.showToast('서비스를 선택해주세요.', 'error');
            return;
        }

        const service = this.serviceDatabase.find(s => s.serviceId === serviceId);
        if (!service) {
            this.showToast('서비스를 찾을 수 없습니다.', 'error');
            return;
        }

        const type = document.getElementById('reservation-type').value;
        const department = document.getElementById('reservation-department').value;
        const datetime = document.getElementById('reservation-datetime').value;
        const symptoms = document.getElementById('reservation-symptoms').value;
        const phone = document.getElementById('reservation-phone').value;

        if (!type || !department || !datetime || !phone) {
            this.showToast('필수 항목을 모두 입력해주세요.', 'error');
            return;
        }

        const reservation = {
            reservationId: Date.now(),
            serviceId: serviceId,
            serviceName: service.name,
            serviceAddress: service.address,
            servicePhone: service.phone,
            type: type,
            typeName: this.getReservationTypeName(type),
            department: department,
            departmentName: this.getDepartmentName(department),
            datetime: datetime,
            symptoms: symptoms,
            phone: phone,
            status: 'PENDING',
            statusName: '예약 대기',
            createdAt: new Date().toISOString()
        };

        this.serviceReservations.unshift(reservation);
        this.saveToStorage();
        this.hideServiceReservationModal();
        this.hideServiceDetailModal();
        this.showToast('예약이 완료되었습니다.', 'success');
    }

    getReservationTypeName(type) {
        const names = {
            'GENERAL': '일반 진료',
            'CHECKUP': '건강검진',
            'FOLLOW_UP': '재진',
            'CONSULTATION': '상담',
            'VACCINATION': '예방접종'
        };
        return names[type] || type;
    }

    getDepartmentName(department) {
        const names = {
            'INTERNAL': '내과',
            'CARDIOLOGY': '심장내과',
            'ENDOCRINOLOGY': '내분비내과',
            'FAMILY': '가정의학과',
            'ORTHOPEDICS': '정형외과',
            'DERMATOLOGY': '피부과',
            'OPHTHALMOLOGY': '안과',
            'ENT': '이비인후과'
        };
        return names[department] || department;
    }

    updatePersonalizedRecommend() {
        const summaryContainer = document.getElementById('personalized-summary');
        const contentContainer = document.getElementById('personalized-content');

        const userHealthConditions = this.getUserHealthConditions();
        const viewedContentIds = this.contentViewHistory.map(h => h.contentId);

        const collaborativeRecommendations = this.collaborativeFiltering(userHealthConditions, viewedContentIds);
        const contentBasedRecommendations = this.contentBasedFiltering(userHealthConditions, viewedContentIds);

        summaryContainer.innerHTML = `
            <div class="personalized-summary-card">
                <h3>맞춤 추천 요약</h3>
                <div class="summary-info">
                    <p>귀하의 건강 상태를 분석하여 맞춤 콘텐츠와 서비스를 추천합니다.</p>
                    ${userHealthConditions.length > 0 ? `
                        <div class="health-conditions">
                            <strong>관심 건강 상태:</strong>
                            ${userHealthConditions.map(c => {
                                const names = {
                                    'HYPERTENSION': '고혈압',
                                    'DIABETES': '당뇨',
                                    'HIGH_CHOLESTEROL': '고콜레스테롤'
                                };
                                return `<span class="condition-tag">${names[c] || c}</span>`;
                            }).join('')}
                        </div>
                    ` : '<p>건강 검진 결과를 업로드하면 더 정확한 추천을 받을 수 있습니다.</p>'}
                </div>
            </div>
        `;

        contentContainer.innerHTML = `
            <div class="recommend-section">
                <h3>협업 필터링 추천</h3>
                <p class="section-description">유사한 건강 상태의 사용자들이 선호한 콘텐츠</p>
                <div class="recommend-list">
                    ${collaborativeRecommendations.slice(0, 3).map(content => `
                        <div class="recommend-item" onclick="window.healthcareApp.viewContent(${content.contentId})">
                            <h4>${content.title}</h4>
                            <p>${content.description}</p>
                            <span class="recommend-reason">유사 사용자 추천</span>
                        </div>
                    `).join('')}
                </div>
            </div>
            <div class="recommend-section">
                <h3>콘텐츠 기반 추천</h3>
                <p class="section-description">귀하의 건강 상태와 관련된 콘텐츠</p>
                <div class="recommend-list">
                    ${contentBasedRecommendations.slice(0, 3).map(content => `
                        <div class="recommend-item" onclick="window.healthcareApp.viewContent(${content.contentId})">
                            <h4>${content.title}</h4>
                            <p>${content.description}</p>
                            <span class="recommend-reason">건강 상태 기반 추천</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }

    collaborativeFiltering(userHealthConditions, viewedContentIds) {
        const similarUsers = this.findSimilarUsers(userHealthConditions);
        const recommendedContentIds = new Set();

        similarUsers.forEach(user => {
            user.viewedContents.forEach(contentId => {
                if (!viewedContentIds.includes(contentId)) {
                    recommendedContentIds.add(contentId);
                }
            });
        });

        return Array.from(recommendedContentIds)
            .map(id => this.contentDatabase.find(c => c.contentId === id))
            .filter(c => c)
            .slice(0, 5);
    }

    findSimilarUsers(userHealthConditions) {
        const mockUsers = [
            {
                userId: 'user2',
                healthConditions: ['HYPERTENSION', 'HIGH_CHOLESTEROL'],
                viewedContents: [1, 3, 4]
            },
            {
                userId: 'user3',
                healthConditions: ['DIABETES'],
                viewedContents: [2, 5]
            },
            {
                userId: 'user4',
                healthConditions: ['HYPERTENSION'],
                viewedContents: [1, 3]
            }
        ];

        return mockUsers.filter(user => {
            const similarity = userHealthConditions.filter(c => user.healthConditions.includes(c)).length;
            return similarity > 0;
        }).sort((a, b) => {
            const simA = userHealthConditions.filter(c => a.healthConditions.includes(c)).length;
            const simB = userHealthConditions.filter(c => b.healthConditions.includes(c)).length;
            return simB - simA;
        });
    }

    contentBasedFiltering(userHealthConditions, viewedContentIds) {
        return this.contentDatabase
            .filter(content => {
                if (viewedContentIds.includes(content.contentId)) return false;
                if (userHealthConditions.length === 0) return true;
                return content.healthConditions.some(c => userHealthConditions.includes(c));
            })
            .sort((a, b) => {
                const matchA = userHealthConditions.filter(c => a.healthConditions.includes(c)).length;
                const matchB = userHealthConditions.filter(c => b.healthConditions.includes(c)).length;
                return matchB - matchA;
            })
            .slice(0, 5);
    }

    initializeDoctorDatabase() {
        return [
            {
                doctorId: 1,
                name: '김의사',
                specialty: '내과',
                hospital: '강남구 내과',
                experience: 15,
                rating: 4.8,
                reviewCount: 127,
                consultationCount: 523,
                specialties: ['고혈압', '당뇨', '고지혈증'],
                available: true,
                profileImage: null
            },
            {
                doctorId: 2,
                name: '이의사',
                specialty: '심장내과',
                hospital: '강남구 심장내과',
                experience: 20,
                rating: 4.9,
                reviewCount: 203,
                consultationCount: 892,
                specialties: ['고혈압', '심장질환', '협심증'],
                available: true,
                profileImage: null
            },
            {
                doctorId: 3,
                name: '박의사',
                specialty: '내분비내과',
                hospital: '강남구 내과',
                experience: 12,
                rating: 4.7,
                reviewCount: 89,
                consultationCount: 345,
                specialties: ['당뇨', '갑상선질환'],
                available: true,
                profileImage: null
            },
            {
                doctorId: 4,
                name: '최의사',
                specialty: '가정의학과',
                hospital: '강남구 한의원',
                experience: 10,
                rating: 4.6,
                reviewCount: 156,
                consultationCount: 412,
                specialties: ['생활습관', '건강관리', '예방의학'],
                available: true,
                profileImage: null
            }
        ];
    }

    updateConsultationScreen() {
        this.updateDoctorListTab();
        this.updateMyConsultationsTab();
    }

    switchConsultationTab(tabName) {
        this.consultationCurrentTab = tabName;
        document.querySelectorAll('#consultation-screen .tab-button').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelectorAll('#consultation-screen .tab-content').forEach(content => {
            content.classList.remove('active');
        });
        document.querySelector(`#consultation-screen [data-tab="${tabName}"]`).classList.add('active');
        document.getElementById(`tab-${tabName}`).classList.add('active');

        if (tabName === 'doctor-list') {
            this.updateDoctorListTab();
        } else if (tabName === 'my-consultations') {
            this.updateMyConsultationsTab();
        }
    }

    updateDoctorListTab() {
        const filtersContainer = document.getElementById('doctor-filters');
        const gridContainer = document.getElementById('doctors-grid');

        filtersContainer.innerHTML = `
            <div class="filter-buttons">
                <button class="filter-btn active" data-specialty="ALL">전체</button>
                <button class="filter-btn" data-specialty="내과">내과</button>
                <button class="filter-btn" data-specialty="심장내과">심장내과</button>
                <button class="filter-btn" data-specialty="내분비내과">내분비내과</button>
                <button class="filter-btn" data-specialty="가정의학과">가정의학과</button>
            </div>
        `;

        filtersContainer.querySelectorAll('.filter-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                filtersContainer.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
                e.target.classList.add('active');
                const specialty = e.target.dataset.specialty;
                this.filterDoctorsBySpecialty(specialty);
            });
        });

        this.filterDoctorsBySpecialty('ALL');
    }

    filterDoctorsBySpecialty(specialty) {
        const gridContainer = document.getElementById('doctors-grid');
        let filteredDoctors = this.doctorDatabase;

        if (specialty !== 'ALL') {
            filteredDoctors = filteredDoctors.filter(d => d.specialty === specialty);
        }

        const userHealthConditions = this.getUserHealthConditions();
        const recommendedDoctors = this.recommendDoctors(filteredDoctors, userHealthConditions);

        gridContainer.innerHTML = '';
        recommendedDoctors.forEach(doctor => {
            const card = document.createElement('div');
            card.className = 'doctor-card';
            card.innerHTML = `
                <div class="doctor-header">
                    <div class="doctor-avatar">
                        <i class="fas fa-user-md"></i>
                    </div>
                    <div class="doctor-info">
                        <h4 class="doctor-name">${doctor.name}</h4>
                        <span class="doctor-specialty">${doctor.specialty}</span>
                        <span class="doctor-hospital">${doctor.hospital}</span>
                    </div>
                </div>
                <div class="doctor-stats">
                    <div class="stat-item">
                        <i class="fas fa-star"></i>
                        <span>${doctor.rating}</span>
                        <span class="stat-label">(${doctor.reviewCount})</span>
                    </div>
                    <div class="stat-item">
                        <i class="fas fa-briefcase"></i>
                        <span>${doctor.experience}년</span>
                    </div>
                    <div class="stat-item">
                        <i class="fas fa-users"></i>
                        <span>상담 ${doctor.consultationCount}건</span>
                    </div>
                </div>
                <div class="doctor-specialties">
                    ${doctor.specialties.map(s => `<span class="specialty-tag">${s}</span>`).join('')}
                </div>
                <div class="doctor-status">
                    <span class="status-badge ${doctor.available ? 'available' : 'unavailable'}">
                        ${doctor.available ? '상담 가능' : '상담 불가'}
                    </span>
                </div>
                <button class="consult-btn" onclick="window.healthcareApp.requestConsultationWithDoctor(${doctor.doctorId})" ${!doctor.available ? 'disabled' : ''}>
                    <i class="fas fa-comments"></i> 상담 신청
                </button>
            `;
            gridContainer.appendChild(card);
        });
    }

    recommendDoctors(doctors, userHealthConditions) {
        return doctors.map(doctor => {
            let score = doctor.rating * 10;
            score += doctor.reviewCount / 10;
            score += doctor.consultationCount / 100;
            score += doctor.experience * 2;

            if (userHealthConditions.length > 0) {
                const matchCount = doctor.specialties.filter(specialty => {
                    if (specialty.includes('고혈압') && userHealthConditions.includes('HYPERTENSION')) return true;
                    if (specialty.includes('당뇨') && userHealthConditions.includes('DIABETES')) return true;
                    if (specialty.includes('고지혈증') && userHealthConditions.includes('HIGH_CHOLESTEROL')) return true;
                    return false;
                }).length;
                score += matchCount * 30;
            }

            if (!doctor.available) {
                score -= 50;
            }

            return { ...doctor, score };
        }).sort((a, b) => b.score - a.score);
    }

    requestConsultationWithDoctor(doctorId) {
        const doctor = this.doctorDatabase.find(d => d.doctorId === doctorId);
        if (!doctor) return;

        const select = document.getElementById('consultation-doctor');
        select.innerHTML = '<option value="">의사를 선택하세요</option>';
        const option = document.createElement('option');
        option.value = doctor.doctorId;
        option.textContent = `${doctor.name} (${doctor.specialty}) - ${doctor.hospital}`;
        option.selected = true;
        select.appendChild(option);

        this.showConsultationRequestModal();
    }

    showConsultationRequestModal() {
        const select = document.getElementById('consultation-doctor');
        select.innerHTML = '<option value="">의사를 선택하세요</option>';
        this.doctorDatabase.forEach(doctor => {
            const option = document.createElement('option');
            option.value = String(doctor.doctorId);
            option.textContent = `${doctor.name} (${doctor.specialty}) - ${doctor.hospital}`;
            select.appendChild(option);
        });
        select.value = '';

        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(10, 0, 0, 0);
        document.getElementById('consultation-datetime').value = tomorrow.toISOString().slice(0, 16);
        document.getElementById('consultation-subject').value = '';
        document.getElementById('consultation-content').value = '';
        document.getElementById('consultation-share-consent').checked = false;

        document.getElementById('consultation-request-modal').classList.remove('hidden');
    }

    hideConsultationRequestModal() {
        document.getElementById('consultation-request-modal').classList.add('hidden');
        document.getElementById('consultation-request-form').reset();
    }

    submitConsultationRequest() {
        if (this.isSubmittingConsultation) {
            return;
        }
        this.isSubmittingConsultation = true;

        const doctorSelect = document.getElementById('consultation-doctor');
        if (!doctorSelect) {
            this.showToast('의사 선택 요소를 찾을 수 없습니다.', 'error');
            this.isSubmittingConsultation = false;
            return;
        }

        const doctorIdValue = doctorSelect.value.trim();
        const shareConsent = document.getElementById('consultation-share-consent') ? document.getElementById('consultation-share-consent').checked : false;
        const type = document.getElementById('consultation-type').value;
        const datetime = document.getElementById('consultation-datetime').value;
        const subject = document.getElementById('consultation-subject').value.trim();
        const content = document.getElementById('consultation-content').value.trim();

        if (!doctorIdValue || doctorIdValue === '') {
            this.showToast('의사를 선택해주세요.', 'error');
            doctorSelect.focus();
            this.isSubmittingConsultation = false;
            return;
        }

        if (!type || type === '') {
            this.showToast('상담 유형을 선택해주세요.', 'error');
            document.getElementById('consultation-type').focus();
            this.isSubmittingConsultation = false;
            return;
        }

        if (!datetime || datetime === '') {
            this.showToast('상담 일시를 선택해주세요.', 'error');
            document.getElementById('consultation-datetime').focus();
            this.isSubmittingConsultation = false;
            return;
        }

        if (!subject || subject === '') {
            this.showToast('상담 주제를 입력해주세요.', 'error');
            document.getElementById('consultation-subject').focus();
            this.isSubmittingConsultation = false;
            return;
        }

        if (!content || content === '') {
            this.showToast('상담 내용을 입력해주세요.', 'error');
            document.getElementById('consultation-content').focus();
            this.isSubmittingConsultation = false;
            return;
        }

        const doctorId = parseInt(doctorIdValue, 10);
        if (isNaN(doctorId) || doctorId <= 0) {
            this.showToast('유효한 의사를 선택해주세요.', 'error');
            doctorSelect.focus();
            this.isSubmittingConsultation = false;
            return;
        }

        const doctor = this.doctorDatabase.find(d => d.doctorId === doctorId || d.doctorId === parseInt(doctorIdValue, 10));
        if (!doctor) {
            this.showToast('선택한 의사를 찾을 수 없습니다.', 'error');
            this.isSubmittingConsultation = false;
            return;
        }

        const consultation = {
            consultationId: Date.now(),
            doctorId: doctorId,
            doctorName: doctor.name,
            doctorSpecialty: doctor.specialty,
            hospital: doctor.hospital,
            type: type,
            typeName: this.getConsultationTypeName(type),
            datetime: datetime,
            subject: subject,
            content: content,
            shareConsent: shareConsent,
            status: 'PENDING',
            statusName: '대기중',
            createdAt: new Date().toISOString()
        };

        this.consultations.unshift(consultation);
        this.saveToStorage();
        this.hideConsultationRequestModal();
        this.isSubmittingConsultation = false;
        this.showToast('상담 신청이 완료되었습니다.', 'success');
        setTimeout(() => {
            this.updateMyConsultationsTab();
        }, 100);
    }

    getConsultationTypeName(type) {
        const names = {
            'GENERAL': '일반 상담',
            'CHECKUP_REVIEW': '검진 결과 상담',
            'MEDICATION': '약물 상담',
            'LIFESTYLE': '생활습관 상담',
            'EMERGENCY': '긴급 상담'
        };
        return names[type] || type;
    }

    updateMyConsultationsTab() {
        const container = document.getElementById('consultation-list-container');

        if (this.consultations.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-comments"></i>
                    <p>신청한 상담이 없습니다.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = '';
        this.consultations.forEach(consultation => {
            const card = document.createElement('div');
            card.className = 'consultation-card';
            card.innerHTML = `
                <div class="consultation-header">
                    <div class="consultation-info">
                        <h4 class="consultation-subject">${consultation.subject}</h4>
                        <span class="consultation-type">${consultation.typeName}</span>
                    </div>
                    <span class="status-badge status-${consultation.status.toLowerCase()}">
                        ${consultation.statusName}
                    </span>
                </div>
                <div class="consultation-details">
                    <div class="detail-row">
                        <i class="fas fa-user-md"></i>
                        <span>${consultation.doctorName} (${consultation.doctorSpecialty})</span>
                    </div>
                    <div class="detail-row">
                        <i class="fas fa-hospital"></i>
                        <span>${consultation.hospital}</span>
                    </div>
                    <div class="detail-row">
                        <i class="fas fa-calendar-alt"></i>
                        <span>${consultation.datetime.replace('T', ' ')}</span>
                    </div>
                </div>
                <div class="consultation-content-preview">
                    ${consultation.content.substring(0, 100)}${consultation.content.length > 100 ? '...' : ''}
                </div>
                <button class="view-consultation-btn" onclick="window.healthcareApp.viewConsultation(${consultation.consultationId})">
                    <i class="fas fa-eye"></i> 상세 보기
                </button>
            `;
            container.appendChild(card);
        });
    }

    viewConsultation(consultationId) {
        const consultation = this.consultations.find(c => c.consultationId === consultationId);
        if (!consultation) return;

        const doctor = this.doctorDatabase.find(d => d.doctorId === consultation.doctorId);

        document.getElementById('consultation-detail-title').textContent = consultation.subject;
        document.getElementById('consultation-detail-content').innerHTML = `
            <div class="consultation-detail-header">
                <div class="detail-info">
                    <span class="consultation-type">${consultation.typeName}</span>
                    <span class="status-badge status-${consultation.status.toLowerCase()}">
                        ${consultation.statusName}
                    </span>
                </div>
            </div>
            <div class="consultation-detail-body">
                <div class="detail-section">
                    <h4>의사 정보</h4>
                    <p><i class="fas fa-user-md"></i> ${consultation.doctorName}</p>
                    <p><i class="fas fa-stethoscope"></i> ${consultation.doctorSpecialty}</p>
                    <p><i class="fas fa-hospital"></i> ${consultation.hospital}</p>
                </div>
                <div class="detail-section">
                    <h4>상담 일시</h4>
                    <p><i class="fas fa-calendar-alt"></i> ${consultation.datetime.replace('T', ' ')}</p>
                </div>
                <div class="detail-section">
                    <h4>상담 주제</h4>
                    <p>${consultation.subject}</p>
                </div>
                <div class="detail-section">
                    <h4>상담 내용</h4>
                    <p>${consultation.content}</p>
                </div>
                ${consultation.response ? `
                <div class="detail-section">
                    <h4>의사 답변</h4>
                    <div class="doctor-response">
                        <p>${consultation.response}</p>
                        <span class="response-date">${consultation.responseDate}</span>
                    </div>
                </div>
                ` : '<p class="no-response">의사 답변을 기다리는 중입니다.</p>'}
                <div class="detail-section">
                    <h4>상담 이력</h4>
                    <div class="consultation-history">
                        ${this.renderConsultationHistory(consultation)}
                    </div>
                </div>
                ${consultation.shareConsent ? `
                <div class="detail-section share-consent-notice">
                    <div class="consent-badge">
                        <i class="fas fa-shield-alt"></i>
                        <strong>보호자에게 동의한 이력입니다</strong>
                    </div>
                </div>
                ` : ''}
            </div>
        `;
        document.getElementById('consultation-detail-modal').classList.remove('hidden');
    }

    renderConsultationHistory(consultation) {
        const history = [];
        
        history.push({
            type: 'requested',
            title: '상담 신청',
            description: '상담이 신청되었습니다.',
            date: consultation.createdAt,
            icon: 'fa-file-medical',
            color: '#2196F3'
        });

        if (consultation.status !== 'PENDING') {
            const statusDate = consultation.statusUpdatedAt || consultation.createdAt;
            history.push({
                type: 'confirmed',
                title: '의사 확인',
                description: `의사가 상담을 확인했습니다. (${consultation.statusName})`,
                date: statusDate,
                icon: 'fa-check-circle',
                color: '#4CAF50'
            });
        }

        if (consultation.response) {
            history.push({
                type: 'responded',
                title: '의사 답변',
                description: '의사가 답변을 작성했습니다.',
                date: consultation.responseDate || consultation.createdAt,
                icon: 'fa-comment-dots',
                color: '#FF9800'
            });
        }

        if (consultation.status === 'COMPLETED') {
            history.push({
                type: 'completed',
                title: '상담 완료',
                description: '상담이 완료되었습니다.',
                date: consultation.completedAt || consultation.responseDate || consultation.createdAt,
                icon: 'fa-check-double',
                color: '#9C27B0'
            });
        }

        history.sort((a, b) => new Date(a.date) - new Date(b.date));

        return history.map((item, index) => {
            const date = new Date(item.date);
            const formattedDate = date.toLocaleString('ko-KR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });

            return `
                <div class="history-timeline-item ${index === history.length - 1 ? 'last' : ''}">
                    <div class="history-timeline-icon" style="background-color: ${item.color}20; color: ${item.color};">
                        <i class="fas ${item.icon}"></i>
                    </div>
                    <div class="history-timeline-content">
                        <div class="history-timeline-title">${item.title}</div>
                        <div class="history-timeline-description">${item.description}</div>
                        <div class="history-timeline-date">${formattedDate}</div>
                    </div>
                </div>
            `;
        }).join('');
    }

    hideConsultationDetailModal() {
        document.getElementById('consultation-detail-modal').classList.add('hidden');
    }

    loadSettings() {
        const saved = localStorage.getItem('healthcare_settings');
        if (saved) {
            return JSON.parse(saved);
        }
        return {
            healthReminder: true,
            checkupNotification: true,
            exerciseReminder: true,
            mealReminder: true,
            dailySteps: 10000,
            weeklyExercise: 3,
            dailyCalories: 2000,
            darkMode: false,
            autoLogin: true,
            language: 'ko',
            biometric: false
        };
    }

    saveSettings() {
        localStorage.setItem('healthcare_settings', JSON.stringify(this.settings));
    }

    updateSettingsScreen() {

        const healthReminder = document.getElementById('setting-health-reminder');
        const checkupNotification = document.getElementById('setting-checkup-notification');
        const exerciseReminder = document.getElementById('setting-exercise-reminder');
        const mealReminder = document.getElementById('setting-meal-reminder');

        if (healthReminder) healthReminder.checked = this.settings.healthReminder;
        if (checkupNotification) checkupNotification.checked = this.settings.checkupNotification;
        if (exerciseReminder) exerciseReminder.checked = this.settings.exerciseReminder;
        if (mealReminder) mealReminder.checked = this.settings.mealReminder;


        const dailySteps = document.getElementById('setting-daily-steps');
        const weeklyExercise = document.getElementById('setting-weekly-exercise');
        const dailyCalories = document.getElementById('setting-daily-calories');

        if (dailySteps) dailySteps.value = this.settings.dailySteps;
        if (weeklyExercise) weeklyExercise.value = this.settings.weeklyExercise;
        if (dailyCalories) dailyCalories.value = this.settings.dailyCalories;


        const darkMode = document.getElementById('setting-dark-mode');
        const autoLogin = document.getElementById('setting-auto-login');
        const language = document.getElementById('setting-language');

        if (darkMode) darkMode.checked = this.settings.darkMode;
        if (autoLogin) autoLogin.checked = this.settings.autoLogin;
        if (language) language.value = this.settings.language;


        const biometric = document.getElementById('setting-biometric');
        if (biometric) biometric.checked = this.settings.biometric;


        this.setupSettingsListeners();
    }

    setupSettingsListeners() {

        const healthReminder = document.getElementById('setting-health-reminder');
        const checkupNotification = document.getElementById('setting-checkup-notification');
        const exerciseReminder = document.getElementById('setting-exercise-reminder');
        const mealReminder = document.getElementById('setting-meal-reminder');

        if (healthReminder) {
            healthReminder.addEventListener('change', (e) => {
                this.settings.healthReminder = e.target.checked;
                this.saveSettings();
            });
        }

        if (checkupNotification) {
            checkupNotification.addEventListener('change', (e) => {
                this.settings.checkupNotification = e.target.checked;
                this.saveSettings();
            });
        }

        if (exerciseReminder) {
            exerciseReminder.addEventListener('change', (e) => {
                this.settings.exerciseReminder = e.target.checked;
                this.saveSettings();
            });
        }

        if (mealReminder) {
            mealReminder.addEventListener('change', (e) => {
                this.settings.mealReminder = e.target.checked;
                this.saveSettings();
            });
        }


        const dailySteps = document.getElementById('setting-daily-steps');
        const weeklyExercise = document.getElementById('setting-weekly-exercise');
        const dailyCalories = document.getElementById('setting-daily-calories');

        if (dailySteps) {
            dailySteps.addEventListener('change', (e) => {
                this.settings.dailySteps = parseInt(e.target.value);
                this.saveSettings();
                this.showToast('일일 걸음 수 목표가 저장되었습니다.', 'success');
            });
        }

        if (weeklyExercise) {
            weeklyExercise.addEventListener('change', (e) => {
                this.settings.weeklyExercise = parseInt(e.target.value);
                this.saveSettings();
                this.showToast('주간 운동 목표가 저장되었습니다.', 'success');
            });
        }

        if (dailyCalories) {
            dailyCalories.addEventListener('change', (e) => {
                this.settings.dailyCalories = parseInt(e.target.value);
                this.saveSettings();
                this.showToast('일일 칼로리 목표가 저장되었습니다.', 'success');
            });
        }


        const darkMode = document.getElementById('setting-dark-mode');
        const autoLogin = document.getElementById('setting-auto-login');
        const language = document.getElementById('setting-language');

        if (darkMode) {
            darkMode.addEventListener('change', (e) => {
                this.settings.darkMode = e.target.checked;
                this.saveSettings();
                this.applyDarkMode(e.target.checked);
                this.showToast(e.target.checked ? '다크 모드가 활성화되었습니다.' : '다크 모드가 비활성화되었습니다.', 'success');
            });
        }

        if (autoLogin) {
            autoLogin.addEventListener('change', (e) => {
                this.settings.autoLogin = e.target.checked;
                this.saveSettings();
            });
        }

        if (language) {
            language.addEventListener('change', (e) => {
                this.settings.language = e.target.value;
                this.saveSettings();
                this.showToast('언어 설정이 변경되었습니다. 앱을 재시작하면 적용됩니다.', 'info');
            });
        }


        const biometric = document.getElementById('setting-biometric');
        if (biometric) {
            biometric.addEventListener('change', (e) => {
                this.settings.biometric = e.target.checked;
                this.saveSettings();
                if (e.target.checked) {
                    this.showToast('생체 인증이 활성화되었습니다.', 'success');
                } else {
                    this.showToast('생체 인증이 비활성화되었습니다.', 'info');
                }
            });
        }
    }

    applyDarkMode(enabled) {
        if (enabled) {
            document.body.classList.add('dark-mode');
        } else {
            document.body.classList.remove('dark-mode');
        }
    }

    exportData() {
        const data = {
            healthData: this.healthData,
            checkupData: this.checkupData,
            dietPlan: this.dietPlan,
            mealRecords: this.mealRecords,
            exerciseProgram: this.exerciseProgram,
            exerciseRecords: this.exerciseRecords,
            consultations: this.consultations,
            serviceReservations: this.serviceReservations,
            exportDate: new Date().toISOString()
        };

        const dataStr = JSON.stringify(data, null, 2);
        const dataBlob = new Blob([dataStr], { type: 'application/json' });
        const url = URL.createObjectURL(dataBlob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `healthcare_data_${new Date().toISOString().split('T')[0]}.json`;
        link.click();
        URL.revokeObjectURL(url);

        this.showToast('데이터가 내보내기되었습니다.', 'success');
    }

    importData() {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'application/json';
        input.onchange = (e) => {
            const file = e.target.files[0];
            if (!file) return;

            const reader = new FileReader();
            reader.onload = (event) => {
                try {
                    const data = JSON.parse(event.target.result);

                    if (data.healthData) this.healthData = data.healthData;
                    if (data.checkupData) this.checkupData = data.checkupData;
                    if (data.dietPlan) this.dietPlan = data.dietPlan;
                    if (data.mealRecords) this.mealRecords = data.mealRecords;
                    if (data.exerciseProgram) this.exerciseProgram = data.exerciseProgram;
                    if (data.exerciseRecords) this.exerciseRecords = data.exerciseRecords;
                    if (data.consultations) this.consultations = data.consultations;
                    if (data.serviceReservations) this.serviceReservations = data.serviceReservations;

                    this.saveToStorage();
                    this.showToast('데이터가 가져와졌습니다.', 'success');
                    this.updateDashboard();
                } catch (error) {
                    this.showToast('데이터 가져오기에 실패했습니다.', 'error');
                }
            };
            reader.readAsText(file);
        };
        input.click();
    }

    clearCache() {
        if (confirm('임시 데이터를 삭제하시겠습니까? 저장된 건강 데이터는 유지됩니다.')) {

            this.showToast('캐시가 삭제되었습니다.', 'success');
        }
    }

    showPrivacyPolicy() {
        alert('개인정보 처리방침\n\n본 앱은 사용자의 건강 데이터를 안전하게 보호합니다.\n\n- 수집하는 정보: 건강 데이터, 검진 결과, 식단 및 운동 기록\n- 사용 목적: 건강 관리 및 맞춤형 서비스 제공\n- 보관 기간: 회원 탈퇴 시까지\n- 제3자 제공: 없음\n\n자세한 내용은 앱 내 고지사항을 참고하세요.');
    }

    showTermsOfService() {
        alert('이용약관\n\n1. 서비스 이용\n- 본 서비스는 건강 관리 목적으로 제공됩니다.\n- 의료 진단을 대체하지 않습니다.\n\n2. 사용자 의무\n- 정확한 건강 정보를 입력해야 합니다.\n- 타인의 정보를 무단으로 사용할 수 없습니다.\n\n3. 책임 제한\n- 본 서비스는 참고용이며, 의료 상담은 전문의에게 받으시기 바랍니다.\n\n4. 서비스 변경\n- 서비스 내용은 사전 고지 없이 변경될 수 있습니다.');
    }

    checkUpdate() {
        this.showToast('최신 버전입니다. (v1.0.0)', 'info');
    }

    showAbout() {
        alert('Healthcare Mono\n\n건강 관리 통합 플랫폼\n\n버전: 1.0.0\n개발: Healthcare Team\n\n건강한 삶을 위한 모든 것을 한 곳에서 관리하세요.\n\n- 실시간 건강 모니터링\n- 건강검진 결과 관리\n- 맞춤형 식단 및 운동 프로그램\n- 의사 상담 및 병원 예약\n- 건강 커뮤니티');
    }

    initializeNotifications() {
        const now = Date.now();
        this.notifications = [
            {
                id: now - 10000,
                type: 'HEALTH_REMINDER',
                title: '건강 데이터 입력 알림',
                message: '오늘의 건강 데이터를 입력해주세요. 혈압, 혈당, 체중을 기록하시면 더 정확한 건강 분석을 제공합니다.',
                read: false,
                createdAt: new Date(now - 300000).toISOString(), 
                action: { type: 'navigate', screen: 'dashboard' }
            },
            {
                id: now - 20000,
                type: 'CHECKUP',
                title: '검진 결과 알림',
                message: '새로운 검진 결과가 업로드되었습니다. 총콜레스테롤 수치가 정상 범위를 벗어났습니다. 자세한 내용을 확인해주세요.',
                read: false,
                createdAt: new Date(now - 1800000).toISOString(), 
                action: { type: 'navigate', screen: 'healthinfo' }
            },
            {
                id: now - 30000,
                type: 'CONSULTATION',
                title: '상담 답변 알림',
                message: '김철수 의사님이 상담에 답변하셨습니다. "고혈압 관리에 대한 상세한 답변을 확인해주세요."',
                read: false,
                createdAt: new Date(now - 3600000).toISOString(), 
                action: { type: 'navigate', screen: 'consultation' }
            },
            {
                id: now - 40000,
                type: 'EXERCISE',
                title: '운동 시간 알림',
                message: '오늘의 운동 시간입니다. 추천 운동: 유산소 운동 30분, 근력 운동 20분',
                read: false,
                createdAt: new Date(now - 7200000).toISOString(), 
                action: { type: 'navigate', screen: 'exercise' }
            },
            {
                id: now - 50000,
                type: 'MEAL',
                title: '식단 기록 알림',
                message: '식사 후 식단을 기록해주세요. 오늘의 칼로리 섭취량을 추적하고 있습니다.',
                read: false,
                createdAt: new Date(now - 10800000).toISOString(), 
                action: { type: 'navigate', screen: 'diet' }
            },
            {
                id: now - 60000,
                type: 'HEALTH_REMINDER',
                title: '건강 점수 업데이트',
                message: '이번 주 건강 점수가 85점으로 업데이트되었습니다. 지난 주 대비 5점 상승했습니다!',
                read: false,
                createdAt: new Date(now - 86400000).toISOString(), 
                action: { type: 'navigate', screen: 'dashboard' }
            },
            {
                id: now - 70000,
                type: 'CHECKUP',
                title: '검진 예약 알림',
                message: '다음 건강검진 예약일이 2주 남았습니다. 병원을 예약하시겠습니까?',
                read: false,
                createdAt: new Date(now - 172800000).toISOString(), 
                action: { type: 'navigate', screen: 'recommendation' }
            },
            {
                id: now - 80000,
                type: 'EXERCISE',
                title: '운동 목표 달성',
                message: '축하합니다! 이번 주 운동 목표를 달성하셨습니다. 다음 주 목표도 화이팅!',
                read: false,
                createdAt: new Date(now - 259200000).toISOString(), 
                action: { type: 'navigate', screen: 'exercise' }
            },
            {
                id: now - 90000,
                type: 'MEAL',
                title: '식단 분석 완료',
                message: '이번 주 식단 분석이 완료되었습니다. 단백질 섭취량이 부족합니다. 식단을 조정해보세요.',
                read: false,
                createdAt: new Date(now - 345600000).toISOString(), 
                action: { type: 'navigate', screen: 'diet' }
            },
            {
                id: now - 100000,
                type: 'CONSULTATION',
                title: '상담 예약 알림',
                message: '내일 오후 2시에 예약하신 상담이 있습니다. 준비사항을 확인해주세요.',
                read: false,
                createdAt: new Date(now - 432000000).toISOString(), 
                action: { type: 'navigate', screen: 'consultation' }
            },
            {
                id: now - 110000,
                type: 'HEALTH_REMINDER',
                title: '건강 데이터 입력 알림',
                message: '어제의 건강 데이터를 입력해주세요.',
                read: true,
                createdAt: new Date(now - 518400000).toISOString(), 
                action: { type: 'navigate', screen: 'dashboard' }
            },
            {
                id: now - 120000,
                type: 'CHECKUP',
                title: '검진 결과 알림',
                message: '건강검진 결과가 업로드되었습니다.',
                read: true,
                createdAt: new Date(now - 604800000).toISOString(), 
                action: { type: 'navigate', screen: 'healthinfo' }
            },
            {
                id: now - 130000,
                type: 'CONSULTATION',
                title: '상담 답변 알림',
                message: '의사님이 상담에 답변하셨습니다.',
                read: true,
                createdAt: new Date(now - 691200000).toISOString(), 
                action: { type: 'navigate', screen: 'consultation' }
            },
            {
                id: now - 140000,
                type: 'EXERCISE',
                title: '운동 프로그램 추천',
                message: '당신의 건강 상태에 맞는 새로운 운동 프로그램이 추천되었습니다.',
                read: true,
                createdAt: new Date(now - 777600000).toISOString(), 
                action: { type: 'navigate', screen: 'exercise' }
            },
            {
                id: now - 150000,
                type: 'MEAL',
                title: '식단 기록 알림',
                message: '식사 후 식단을 기록해주세요.',
                read: true,
                createdAt: new Date(now - 864000000).toISOString(), 
                action: { type: 'navigate', screen: 'diet' }
            },
            {
                id: now - 160000,
                type: 'SYSTEM',
                title: '시스템 업데이트',
                message: '앱이 최신 버전으로 업데이트되었습니다. 새로운 기능을 확인해보세요.',
                read: true,
                createdAt: new Date(now - 950400000).toISOString(), 
                action: null
            },
            {
                id: now - 170000,
                type: 'WARNING',
                title: '건강 주의 알림',
                message: '혈압 수치가 정상 범위를 벗어났습니다. 의사와 상담을 권장합니다.',
                read: true,
                createdAt: new Date(now - 1036800000).toISOString(), 
                action: { type: 'navigate', screen: 'consultation' }
            },
            {
                id: now - 180000,
                type: 'SUCCESS',
                title: '목표 달성',
                message: '체중 감량 목표를 달성하셨습니다! 축하합니다.',
                read: true,
                createdAt: new Date(now - 1123200000).toISOString(), 
                action: { type: 'navigate', screen: 'profile' }
            },
            {
                id: now - 190000,
                type: 'HEALTH_REMINDER',
                title: '건강 데이터 입력 알림',
                message: '오늘의 건강 데이터를 입력해주세요.',
                read: true,
                createdAt: new Date(now - 1209600000).toISOString(), 
                action: { type: 'navigate', screen: 'dashboard' }
            },
            {
                id: now - 200000,
                type: 'CHECKUP',
                title: '검진 예약 알림',
                message: '건강검진 예약일이 다가옵니다.',
                read: true,
                createdAt: new Date(now - 1296000000).toISOString(), 
                action: { type: 'navigate', screen: 'recommendation' }
            }
        ];
        this.saveNotifications();
        this.updateNotificationBadge();
    }

    saveNotifications() {
        localStorage.setItem('healthcare_notifications', JSON.stringify(this.notifications));
    }

    loadNotifications() {
        const saved = localStorage.getItem('healthcare_notifications');
        if (saved) {
            this.notifications = JSON.parse(saved);
        } else {
            this.initializeNotifications();
        }
        this.updateNotificationBadge();
    }

    addNotification(type, title, message, action = null) {
        const notification = {
            id: Date.now(),
            type: type,
            title: title,
            message: message,
            read: false,
            createdAt: new Date().toISOString(),
            action: action
        };
        this.notifications.unshift(notification);
        this.saveNotifications();
        this.updateNotificationBadge();
    }

    updateNotificationBadge() {
        const unreadCount = this.notifications.filter(n => !n.read).length;
        const badge = document.getElementById('notification-badge');
        const menuBadge = document.getElementById('notification-badge-menu');

        if (badge) {
            badge.textContent = unreadCount;
            badge.style.display = unreadCount > 0 ? 'flex' : 'none';
        }

        if (menuBadge) {
            menuBadge.textContent = unreadCount;
            menuBadge.style.display = unreadCount > 0 ? 'inline-flex' : 'none';
        }
    }

    updateNotificationScreen() {
        const container = document.getElementById('notification-list-page');
        if (!container) return;


        const readFilter = document.querySelector('#notification-filters .filter-btn[data-filter].active')?.dataset.filter || 'all';
        const typeFilter = document.querySelector('#notification-filters .filter-btn[data-type].active')?.dataset.type || 'all';

        let filteredNotifications = [...this.notifications];


        if (readFilter === 'unread') {
            filteredNotifications = filteredNotifications.filter(n => !n.read);
        } else if (readFilter === 'read') {
            filteredNotifications = filteredNotifications.filter(n => n.read);
        }


        if (typeFilter !== 'all') {
            filteredNotifications = filteredNotifications.filter(n => n.type === typeFilter);
        }


        filteredNotifications.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

        if (filteredNotifications.length === 0) {
            container.innerHTML = `
                <div class="empty-notifications">
                    <i class="fas fa-bell-slash"></i>
                    <p>알림이 없습니다.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = filteredNotifications.map(notification => {
            const timeAgo = this.getTimeAgo(notification.createdAt);
            const icon = this.getNotificationIcon(notification.type);
            const readClass = notification.read ? 'read' : 'unread';

            return `
                <div class="notification-item ${readClass}" onclick="window.healthcareApp.handleNotificationClick(${notification.id})">
                    <div class="notification-icon">
                        <i class="${icon}"></i>
                    </div>
                    <div class="notification-content">
                        <div class="notification-title">
                            ${notification.title}
                            ${!notification.read ? '<span class="notification-unread-badge">NEW</span>' : ''}
                        </div>
                        <div class="notification-message">${notification.message}</div>
                        <div class="notification-time">${timeAgo}</div>
                    </div>
                    ${!notification.read ? '<div class="notification-unread-dot"></div>' : ''}
                </div>
            `;
        }).join('');
    }

    getNotificationIcon(type) {
        const icons = {
            'HEALTH_REMINDER': 'fas fa-heartbeat',
            'CHECKUP': 'fas fa-file-medical',
            'CONSULTATION': 'fas fa-user-md',
            'EXERCISE': 'fas fa-dumbbell',
            'MEAL': 'fas fa-utensils',
            'SYSTEM': 'fas fa-info-circle',
            'WARNING': 'fas fa-exclamation-triangle',
            'SUCCESS': 'fas fa-check-circle'
        };
        return icons[type] || 'fas fa-bell';
    }

    getTimeAgo(dateString) {
        const now = new Date();
        const date = new Date(dateString);
        const diff = now - date;
        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(diff / 3600000);
        const days = Math.floor(diff / 86400000);

        if (minutes < 1) return '방금 전';
        if (minutes < 60) return `${minutes}분 전`;
        if (hours < 24) return `${hours}시간 전`;
        if (days < 7) return `${days}일 전`;
        return date.toLocaleDateString('ko-KR');
    }

    handleNotificationClick(notificationId) {
        const notification = this.notifications.find(n => n.id === notificationId);
        if (!notification) return;


        if (!notification.read) {
            notification.read = true;
            this.saveNotifications();
            this.updateNotificationBadge();
            this.updateNotificationScreen();
        }


        if (notification.action) {
            if (notification.action.type === 'navigate') {
                this.navigateToScreen(notification.action.screen);
            }
        }
    }

    markAllNotificationsAsRead() {
        this.notifications.forEach(notification => {
            notification.read = true;
        });
        this.saveNotifications();
        this.updateNotificationBadge();
        this.updateNotificationScreen();
        this.showToast('모든 알림이 읽음 처리되었습니다.', 'success');
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
