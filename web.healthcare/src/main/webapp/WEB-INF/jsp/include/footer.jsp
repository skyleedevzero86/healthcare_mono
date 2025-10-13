<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<footer class="footer_wrap">
  <div class="footer">
    <div class="ft_logo_wrap _relative">
      <div
        class="swiper swiper-container swiper-container-initialized swiper-container-horizontal"
      >
        <div class="_swiper_wrap swiper-wrapper">
          <div class="ft_logo swiper-slide">
            <a href="https://https://www.kdca.go.kr" target="_blank"
              ><img src="/images/main/fg_kdca.png" alt="질병청"
            /></a>
          </div>
        </div>
        <div class="swiper-button-prev"></div>
        <div class="swiper-button-next"></div>
      </div>
    </div>

    <div class="ft_content_wrap">
      <section class="ft_info_wrap">
        <div class="fnb_wrap">
          <ul class="fnb">
            <li class="fnb__item">
              <a href="#none" class="fnb__link">저작권보호정책ㅣ</a>
            </li>
            <li class="fnb__item">
              <a href="#none" class="fnb__link">이메일무단수집거부</a>
            </li>
          </ul>
        </div>
        <address class="ft_addr_wrap">
          <span>광주광역시 광주구 XXXX</span>
          <span>copyright ⓒ sleekydz86 all rights reserved.</span>
        </address>
      </section>
      <section class="footer_relation_wrap">
        <select
          name="siteSelect"
          id="openSite"
          class="footer_relation__select"
          title="관련사이트 바로가기"
        >
          <option
            value="javascript:void(0);"
            selected="selected"
            disabled="disabled"
          >
            관련사이트 바로가기
          </option>
          <option value="http://namuict.co.kr/">NAMU ICT</option>
          <option value="http://https://www.kdca.go.kr/">질병청</option>
        </select>
        <button
          type="button"
          class="footer_relation__btn"
          title="새 창으로 열림"
          onclick="javascript:openSite();"
        >
          이동
        </button>
      </section>
    </div>
  </div>
</footer>

<script>
  const serachSwiper01 = new Swiper(".search_swiper01", {
    breakpoints: {
      768: {
        spaceBetween: 34,
        slidesPerView: 7,
      },
      320: {
        spaceBetween: 20,
        slidesPerView: "auto",
        slidesPerGroup: 1,
      },
    },
    centeredSlides: false,
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    touchRatio: 0,
    speed: 1000,
    autoplay: {
      delay: 3000,
      pauseOnMouseEnter: false,
      disableOnInteraction: false,
    },
  });
  const serachSwiper02 = new Swiper(".search_swiper02", {
    breakpoints: {
      768: {
        spaceBetween: 34,
        slidesPerView: 7,
      },
      320: {
        spaceBetween: 20,
        slidesPerView: "auto",
        slidesPerGroup: 1,
      },
    },
    centeredSlides: false,
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    touchRatio: 0,
    speed: 1000,
    autoplay: {
      delay: 3000,
      pauseOnMouseEnter: false,
      disableOnInteraction: false,
    },
  });
  const serachSwiper03 = new Swiper(".search_swiper03", {
    breakpoints: {
      768: {
        spaceBetween: 34,
        slidesPerView: 7,
      },
      320: {
        spaceBetween: 20,
        slidesPerView: "auto",
        slidesPerGroup: 1,
      },
    },
    centeredSlides: false,
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    touchRatio: 0,
    speed: 1000,
    autoplay: {
      delay: 3000,
      pauseOnMouseEnter: true,
      disableOnInteraction: false,
    },
  });
  const serachSwiper04 = new Swiper(".search_swiper04", {
    breakpoints: {
      768: {
        spaceBetween: 34,
        slidesPerView: 7,
      },
      320: {
        spaceBetween: 20,
        slidesPerView: "auto",
        slidesPerGroup: 1,
      },
    },
    centeredSlides: false,
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    touchRatio: 0,
    speed: 1000,
    autoplay: {
      delay: 3000,
      pauseOnMouseEnter: true,
      disableOnInteraction: false,
    },
  });
  const serachSwiper05 = new Swiper(".search_swiper05", {
    breakpoints: {
      768: {
        spaceBetween: 34,
        slidesPerView: 7,
      },
      320: {
        spaceBetween: 20,
        slidesPerView: "auto",
        slidesPerGroup: 1,
      },
    },
    centeredSlides: false,
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    touchRatio: 0,
    speed: 1000,
    autoplay: {
      delay: 3000,
      pauseOnMouseEnter: true,
      disableOnInteraction: false,
    },
  });
  const serachSwiper06 = new Swiper(".search_swiper06", {
    breakpoints: {
      768: {
        spaceBetween: 34,
        slidesPerView: 7,
      },
      320: {
        spaceBetween: 20,
        slidesPerView: "auto",
        slidesPerGroup: 1,
      },
    },
    centeredSlides: false,
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    touchRatio: 0,
    speed: 1000,
    autoplay: {
      delay: 3000,
      pauseOnMouseEnter: true,
      disableOnInteraction: false,
    },
  });
  const serachSwiper07 = new Swiper(".search_swiper07", {
    breakpoints: {
      768: {
        spaceBetween: 34,
        slidesPerView: 7,
      },
      320: {
        spaceBetween: 20,
        slidesPerView: "auto",
        slidesPerGroup: 1,
      },
    },
    centeredSlides: false,
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    touchRatio: 0,
    speed: 1000,
    autoplay: {
      delay: 3000,
      pauseOnMouseEnter: true,
      disableOnInteraction: false,
    },
  });

  const swiper = new Swiper(".swiper", {
    breakpoints: {
      768: {
        spaceBetween: 30,
        slidesPerView: 5,
        loopAdditionalSlides: 1,
        loopedSlides: 2,
      },
      320: {
        spaceBetween: 20,
        slidesPerView: "auto",
        slidesPerGroup: 1,
        loopAdditionalSlides: 1,
        loopedSlides: 2,
      },
    },
    centeredSlides: true,
    loop: true,
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    speed: 1000,
    autoplay: {
      delay: 5000,
      pauseOnMouseEnter: true,
      disableOnInteraction: false,
    },
  });

  $(document).ready(function () {
    $(".swiper-slide").on("mouseover", function () {
      swiper.autoplay.stop();
    });
    $(".swiper-slide").on("mouseout", function () {
      swiper.autoplay.start();
    });
  });
</script>
