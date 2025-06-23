package com.rcdnc.cafezinho.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rcdnc.cafezinho.screens.auth.signup_pages.SignupPage1
import kotlinx.coroutines.coroutineScope

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
) {
    // Cria um PagerState para gerenciar o estado do pager
    val pagerState = rememberPagerState(pageCount = { 7 })

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState, // Usa o PagerState para controlar o pager
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false // Mantém a navegação controlada programaticamente
        ) { page ->
            // Mapeia cada página para o respectivo composable
            when (page) {
                0 -> SignupPage1(
                    onNext = {
                        // Navega para a próxima página
                        coroutineScope.lau { pagerState.animateScrollToPage(1) }
                    }
                )
//                1 -> SignupPage2(
//                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
//                    onBack = { coroutineScope.launch { pagerState.animateScrollToPage(0) } }
//                )
//                2 -> SignupPage3(
//                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(3) } },
//                    onBack = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
//                )
//                3 -> SignupPage4(
//                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(4) } },
//                    onBack = { coroutineScope.launch { pagerState.animateScrollToPage(2) } }
//                )
//                4 -> SignupPage5(
//                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(5) } },
//                    onBack = { coroutineScope.launch { pagerState.animateScrollToPage(3) } }
//                )
//                5 -> SignupPage6(
//                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(6) } },
//                    onBack = { coroutineScope.launch { pagerState.animateScrollToPage(4) } }
//                )
//                6 -> SignupPage7(
//                    onNext = { onSignupSuccess() }, // Chama o callback ao concluir
//                    onBack = { coroutineScope.launch { pagerState.animateScrollToPage(5) } }
//                )
            }
        }
    }
}