//package com.rcdnc.cafezinho.data.remote
//
//import kotlin.jvm.JvmField
//
///**
// * This object provides functionality to initialize the URLS for the ApiService.
// * It determines the appropriate base URL based on the application's current environment setting.
// *
// * Usage of this object should be limited to the initialization phase of the application,
// * typically during the setup or configuration stage.
// */
//object ApiUrl {
//
//    @JvmField
//    var useCoin: String = "${ApiService.baseUrl}consumable/use-coin"
//
//    @JvmField
//    var verifySubscriptions: String = "${ApiService.baseUrl}subscription/verify"
//
//    @JvmField
//    var verifyImages: String = "${ApiService.baseUrl}user/verifyImages/"
//
//    @JvmField
//    var setLiveUserStatus: String = "${ApiService.baseUrl}live/"
//
//    @JvmField
//    var saveFilterMetric: String = "${ApiService.baseUrl}filter"
//
//    @JvmField
//    var boostPurchase: String = "${ApiService.baseUrl}consumable/boost"
//
//    @JvmField
//    var showSexualOrientation: String = "${ApiService.baseUrl}sexualOrientations/"
//
//    @JvmField
//    var newAccess: String = "${ApiService.baseUrl}user/activity/"
//
//    @JvmField
//    val randomQuestion: String = "${ApiService.baseUrl}cafeteria/randomQuestion"
//
//    @JvmField
//    val allPairsFromCafeteria: String = "${ApiService.baseUrl}inbox/allPairsFromCafeteria/"
//
//    /// must put the provider infront the auth
//    @JvmField
//    val authenticateUser: String = "${ApiService.baseUrl}auth/"
//
//    @JvmField
//    val registerSession: String = "${ApiService.baseUrl}registerSession"
//
//    @JvmField
//    val registerUser: String = "${ApiService.baseUrl}register/"
//
//    @JvmField
//    val logoutUser: String = "${ApiService.baseUrl}user/revoking/"
//
//    @JvmField
//    val logDebugUser: String = "${ApiService.baseUrl}LogDebug"
//
//    @JvmField
//    val interactions: String = "${ApiService.baseUrl}interactions"
//
//    @JvmField
//    val showLiveControl: String = "${ApiService.baseUrl}live/control/"
//
//    @JvmField
//    var cancelRegisterSubscriptionUser: String = "${ApiService.baseUrl}live/"
//
//    @JvmField
//    val showPassions: String = "${ApiService.baseUrl}passions/"
//
//    @JvmField
//    val feedbackUninstall: String = "${ApiService.baseUrl}feedback/uninstall"
//
//    @JvmField
//    val followUp: String = "${ApiService.baseUrl}followUp/"
//
//    @JvmField
//    val showFeedbackOptions: String = "${ApiService.baseUrl}feedback/options"
//
//    @JvmField
//    val showFeedbackCheckbox = "${ApiService.baseUrl}feedback/checkbox/"
//
//    @JvmField
//    val deleteChat: String = "${ApiService.baseUrl}inbox/"
//
//    @JvmField
//    val deleteMatch: String = "${ApiService.baseUrl}match/"
//
//    @JvmField
//    val blockUser: String = "${ApiService.baseUrl}changeBlockStatus"
//
//    @JvmField
//    val editProfile: String = "${ApiService.baseUrl}user/"
//
//    @JvmField
//    val retrieveSocials: String = "${ApiService.baseUrl}user/"
//
//    @JvmField
//    val showUserMatches: String = "${ApiService.baseUrl}match/"
//
//    @JvmField
//    val showReportReasons: String = "${ApiService.baseUrl}reportReasons"
//
//    @JvmField
//    val rewindUser: String = "${ApiService.baseUrl}consumable/like/"
//
//    @JvmField
//    val showUserDetail: String = "${ApiService.baseUrl}user/"
//
//    @JvmField
//    val showSchools: String = "${ApiService.baseUrl}school/name/"
//
//    @JvmField
//    val showSexualOrientations: String = "${ApiService.baseUrl}sexualOrientations"
//
//    @JvmField
//    val findUserWithParams: String = "${ApiService.baseUrl}user/"
//
//    @JvmField
//    val findNearByUsers: String = "${ApiService.baseUrl}user/showNearByUsers/"
//
//    @JvmField
//    val registerFilters: String = "${ApiService.baseUrl}userFilters/"
//
//    @JvmField
//    val uploadUserImage: String = "${ApiService.baseUrl}image/upload/"
//
//    @JvmField
//    val uploadUserImageBin: String = "${ApiService.baseUrl}image/uploadBinary/"
//
//    @JvmField
//    val imageChangeOrder: String = "${ApiService.baseUrl}image/changeOrder/"
//
//    @JvmField
//    val deleteUserImage: String = "${ApiService.baseUrl}image/delete/"
//
//    @JvmField
//    val showTopUsers: String = "${ApiService.baseUrl}user/getTopUsers/"
//
//    @JvmField
//    val likeUser: String = "${ApiService.baseUrl}consumable/like"
//
//    @JvmField
//    val getPersonalities = "${ApiService.baseUrl}personality"
//
//    @JvmField
//    val twoUsersHaveChattedBefore = "${ApiService.baseUrl}inbox/twoUsersHaveChattedBefore"
//
//    @JvmField
//    val instagramRedirect: String = "${ApiService.baseUrl}instagram/redirect-auth"
//
//    @JvmField
//    val instagramCallback: String = "${ApiService.baseUrl}instagram/callback"
//
//    @JvmField
//    val reportUser = "${ApiService.baseUrl}reportUser"
//
//    @JvmField
//    val retrieveAllQuizQuestions = "${ApiService.baseUrl}liveQuestion/"
//
//    @JvmField
//    val registerSubscriptionUser = "${ApiService.baseUrl}live/register-subscription/"
//
//    @JvmField
//    val deleteUserAccount: String = "${ApiService.baseUrl}user/"
//
//    @JvmField
//    val instagramData: String = "${ApiService.baseUrl}instagram/media/"
//
//    @JvmField
//    val showUserLikes: String = "${ApiService.baseUrl}likeUser/"
//
//    @JvmField
//    val showUserDistinguished: String = "${ApiService.baseUrl}distinguished/"
//
//    @JvmField
//    val liveFeedback: String = "${ApiService.baseUrl}feedback/live"
//
//    @JvmField
//    val showOnlineUsers: String = "${ApiService.baseUrl}user/showOnlineUsers/"
//
//    @JvmField
//    val countNewLikes: String = "${ApiService.baseUrl}like/"
//
//    @JvmField
//    val boostProfile: String = "${ApiService.baseUrl}consumable/boost/"
//
//    @JvmField
//    val internalFeedback = "${ApiService.baseUrl}feedback/internal"
//
//    @JvmField
//    val showUserByEmail = "${ApiService.baseUrl}user/email"
//
//    @JvmField
//    val getRecommendedUsersDetails = "${ApiService.baseUrl}live/recommended-users"
//
//    @JvmField
//    val insertQuizAnswer = "${ApiService.baseUrl}liveAnswer"
//
//    @JvmField
//    val updatePassions: String = "${ApiService.baseUrl}passions/"
//
//
//    @JvmField
//    val profileViews: String = "${ApiService.baseUrl}views/"
//
//    @JvmField
//    val findUserScreenShow: String = "${ApiService.baseUrl}findUserScreenShow/"
//
//    @JvmField
//    val updateMessageCounts: String = "${ApiService.baseUrl}match/"
//
//    @JvmField
//    val getIdsOfPassions = "${ApiService.baseUrl}passions/"
//
//    @JvmField
//    val getUsersToSendNews = "${ApiService.baseUrl}user/getUsersToSendNews"
//
//    @JvmField
//    val showBlockedUser = "${ApiService.baseUrl}blockedUsers/"
//
//    @JvmField
//    val startSubscription: String = "${ApiService.baseUrl}subscription"
//
//    @JvmField
//    val setLiveUserInterest = "${ApiService.baseUrl}live/"
//
//    @JvmField
//    val sendMessageNotification = "${ApiService.baseUrl}notification/"
//
//    @JvmField
//    val sendMessagePreMatchNotification = "${ApiService.baseUrl}notification/preMatch/"
//
//    @JvmField
//    val retrieveQuizQuestions: String = "${ApiService.baseUrl}liveQuestion/"
//
//    @JvmField
//    val purchaseCoin: String = "${ApiService.baseUrl}consumable/purchaseCoin"
//
//    @JvmField
//    val getChatRestrictionForUser: String = "${ApiService.baseUrl}live/chat-restriction"
//
//    @JvmField
//    val updateUserSession: String = "${ApiService.baseUrl}session"
//
//    @JvmField
//    val checkSubscription: String = "${ApiService.baseUrl}subscription/"
//
//    @JvmField
//    val notificationError: String = "${ApiService.baseUrl}notification/error"
//
//    @JvmField
//    val rating: String = "${ApiService.baseUrl}rating"
//
//    @JvmField
//    val verifyProfilePhoto: String = "${ApiService.baseUrl}verifyProfilePhoto"
//
//
//    @JvmField
//    val liveAdminFunctions: String = "${ApiService.baseUrl}liveAdminFunctions"
//
//    @JvmField
//    val countAfterFilter: String = "${ApiService.baseUrl}countAfterFilter"
//
//    @JvmField
//    val rewardConsumable: String = "${ApiService.baseUrl}consumable/reward"
//
//
//
//    /**
//     * Determines and returns the appropriate base URL based on the environment type.
//     * This function reads the 'ambiente' property and selects the URL accordingly.
//     *
//     * Supported environment types are 'dev', 'stage', 'prod', and 'test'.
//     *
//     * @param context The context used to load the configuration properties.
//     * @return The base URL string for the given environment.
//     * @throws IOException If there is an issue with loading the properties file.
//     * @throws IllegalStateException If the 'ambiente' property is not set in the properties file.
//     * @throws IllegalArgumentException If the 'ambiente' property value is not one of the supported types.
//     */
//    object Provider {
//        fun getBaseUrl(): String {
//            val environmentType = com.rcdnc.cafezinho.config.BuildKonfig.ambiente
//
//            return when (environmentType) {
//                "dev" -> "https://${com.rcdnc.cafezinho.config.BuildKonfig.API_IP}/api/"
//                "stage" -> "https://ds3rvbo91hhlz.cloudfront.net/api/"
//                "stage-prod" -> "https://d1vgw63bqjgrhr.cloudfront.net/api/"
//                "prod" -> "https://d2pwhpqxnn6p0k.cloudfront.net/api/"
//                else -> throw IllegalArgumentException("Ambiente não reconhecido: $environmentType")
//            }
//        }
//    }
//}