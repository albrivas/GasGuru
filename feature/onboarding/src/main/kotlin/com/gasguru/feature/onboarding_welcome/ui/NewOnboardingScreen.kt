package com.gasguru.feature.onboarding_welcome.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.onboarding.R
import com.gasguru.feature.onboarding_welcome.viewmodel.NewOnboardingUiState
import com.gasguru.feature.onboarding_welcome.viewmodel.NewOnboardingViewModel

@Composable
fun NewOnboardingScreenRoute(
    viewModel: NewOnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NewOnboardingScreen(
        uiState = uiState,
        onEvent = viewModel::handleEvent,
    )
}

@Composable
internal fun NewOnboardingScreen(
    uiState: NewOnboardingUiState,
    onEvent: (NewOnboardingEvent) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { uiState.totalPages })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .collect { page -> onEvent(NewOnboardingEvent.PageChanged(page = page)) }
    }

    LaunchedEffect(uiState.currentPage) {
        if (pagerState.settledPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GasGuruTheme.colors.neutralWhite)
            .systemBarsPadding(),
    ) {
        OnboardingTopBar(
            currentPage = uiState.currentPage,
            totalPages = uiState.totalPages,
            showSkip = uiState.showSkipButton,
            onSkip = { onEvent(NewOnboardingEvent.Skip) },
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            OnboardingPageContent(
                page = OnboardingPage.entries[page],
            )
        }

        OnboardingBottomSection(
            currentPage = uiState.currentPage,
            totalPages = uiState.totalPages,
            isFirstPage = uiState.isFirstPage,
            isLastPage = uiState.isLastPage,
            onNext = { onEvent(NewOnboardingEvent.NextPage) },
            onBack = { onEvent(NewOnboardingEvent.PreviousPage) },
        )
    }
}

@Composable
private fun OnboardingTopBar(
    currentPage: Int,
    totalPages: Int,
    showSkip: Boolean,
    onSkip: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${currentPage + 1}",
                color = GasGuruTheme.colors.primary500,
                style = GasGuruTheme.typography.smallBold,
            )
            Text(
                text = "/",
                color = GasGuruTheme.colors.neutral400,
                style = GasGuruTheme.typography.smallRegular,
            )
            Text(
                text = "$totalPages",
                color = GasGuruTheme.colors.neutral700,
                style = GasGuruTheme.typography.smallRegular,
            )
        }
        Text(
            text = stringResource(R.string.onboarding_skip),
            color = GasGuruTheme.colors.neutral700,
            style = GasGuruTheme.typography.smallRegular,
            modifier = Modifier
                .alpha(if (showSkip) 1f else 0f)
                .then(if (showSkip) Modifier.clickable(onClick = onSkip) else Modifier)
                .padding(8.dp),
        )
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Spacer(modifier = Modifier.weight(2f))

        Image(
            painter = painterResource(id = page.iconRes),
            contentDescription = null,
            modifier = Modifier
                .heightIn(max = 180.dp)
                .wrapContentWidth()
                .padding(horizontal = 64.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(page.titleRes),
            color = GasGuruTheme.colors.neutralBlack,
            style = GasGuruTheme.typography.h3,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(page.descriptionRes),
            color = GasGuruTheme.colors.textSubtle,
            style = GasGuruTheme.typography.baseRegular,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.weight(4f))
    }
}

@Composable
private fun OnboardingBottomSection(
    currentPage: Int,
    totalPages: Int,
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        OnboardingProgressDots(
            currentPage = currentPage,
            totalPages = totalPages,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!isFirstPage) {
                Text(
                    text = stringResource(R.string.onboarding_back),
                    color = GasGuruTheme.colors.neutral700,
                    style = GasGuruTheme.typography.smallRegular,
                    modifier = Modifier
                        .clickable(onClick = onBack)
                        .padding(8.dp),
                )
            }
            GasGuruButton(
                onClick = onNext,
                text = stringResource(
                    if (isLastPage) R.string.onboarding_start else R.string.onboarding_continue,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OnboardingProgressDots(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalPages) { index ->
            val isActive = index <= currentPage
            val isCurrent = index == currentPage

            val width by animateDpAsState(
                targetValue = if (isCurrent) 24.dp else 8.dp,
                label = "dotWidth",
            )
            val color by animateColorAsState(
                targetValue = if (isActive) {
                    GasGuruTheme.colors.primary500
                } else {
                    GasGuruTheme.colors.neutral400
                },
                label = "dotColor",
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp),
                    ),
            )
        }
    }
}

@Composable
@ThemePreviews
private fun NewOnboardingScreenPreview() {
    MyApplicationTheme {
        NewOnboardingScreen(
            uiState = NewOnboardingUiState(),
            onEvent = {},
        )
    }
}

@Composable
@ThemePreviews
private fun NewOnboardingScreenLastPagePreview() {
    MyApplicationTheme {
        NewOnboardingScreen(
            uiState = NewOnboardingUiState(currentPage = 4),
            onEvent = {},
        )
    }
}