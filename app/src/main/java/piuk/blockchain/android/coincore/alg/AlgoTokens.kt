package piuk.blockchain.android.coincore.alg

import com.blockchain.logging.CrashLogger
import com.blockchain.preferences.CurrencyPrefs
import com.blockchain.swap.nabu.datamanagers.CustodialWalletManager
import com.blockchain.wallet.DefaultLabels
import info.blockchain.balance.CryptoCurrency
import io.reactivex.Completable
import io.reactivex.Single
import piuk.blockchain.android.coincore.CryptoAddress
import piuk.blockchain.android.coincore.SingleAccount
import piuk.blockchain.android.coincore.SingleAccountList
import piuk.blockchain.android.coincore.ReceiveAddress
import piuk.blockchain.android.coincore.impl.CryptoAssetBase
import piuk.blockchain.android.thepit.PitLinking
import piuk.blockchain.androidcore.data.charts.ChartsDataManager
import piuk.blockchain.androidcore.data.exchangerate.ExchangeRateDataManager
import timber.log.Timber

internal class AlgoAsset(
    custodialManager: CustodialWalletManager,
    exchangeRates: ExchangeRateDataManager,
    historicRates: ChartsDataManager,
    currencyPrefs: CurrencyPrefs,
    labels: DefaultLabels,
    pitLinking: PitLinking,
    crashLogger: CrashLogger
) : CryptoAssetBase(
    exchangeRates,
    historicRates,
    currencyPrefs,
    labels,
    custodialManager,
    pitLinking,
    crashLogger
) {

    override val asset: CryptoCurrency
        get() = CryptoCurrency.ALGO

    override fun initToken(): Completable =
        Completable.complete()

    override fun loadNonCustodialAccounts(labels: DefaultLabels): Single<SingleAccountList> =
        Single.fromCallable {
            listOf(getAlgoAccount())
        }
        .doOnError { Timber.e(it) }
        .onErrorReturn { emptyList() }

    private fun getAlgoAccount(): SingleAccount =
        AlgoCryptoWalletAccount(label = labels.getDefaultNonCustodialWalletLabel(asset),
            exchangeRates = exchangeRates)

    override fun loadCustodialAccount(): Single<SingleAccountList> =
        Single.just(
            listOf(AlgoCustodialTradingAccount(
                asset,
                labels.getDefaultCustodialWalletLabel(asset),
                exchangeRates,
                custodialManager
            ))
        )

    override fun parseAddress(address: String): ReceiveAddress? = null
}

internal class AlgoAddress(
    override val address: String,
    override val label: String = address
) : CryptoAddress {
    override val asset: CryptoCurrency = CryptoCurrency.ALGO
}