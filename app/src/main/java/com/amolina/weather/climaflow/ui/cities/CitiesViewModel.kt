package com.amolina.weather.climaflow.ui.cities

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableArrayList
import com.amolina.weather.climaflow.utils.Log
import com.amolina.weather.climaflow.data.DataManager
import com.amolina.weather.climaflow.data.model.db.City
import com.amolina.weather.climaflow.rx.SchedulerProvider
import com.amolina.weather.climaflow.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject


/**
 * Created by Amolina on 02/02/17.
 */

class CitiesViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<Any>(dataManager, schedulerProvider) {

    val TAG: String = this::class.java.simpleName
    val showCities: MutableLiveData<List<CitiesItemModel>> = MutableLiveData()
    val actionsSubject: PublishSubject<CitiesActions> = PublishSubject.create()
    val citiesItemViewModels = ObservableArrayList<CitiesItemModel>()
    val cityDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val cityAdded: MutableLiveData<Boolean> = MutableLiveData()


    fun getSearchedCities(search: String) {
        setIsLoading(true)
       //Log.d(TAG, "checkNearestCity")
        compositeDisposable.add(
            combineSearchedCities(search)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(getCitiesObserver())
        )
    }

    fun getNearAndSelectedAllCities() {
        setIsLoading(true)
       //Log.d(TAG, "checkNearestCity")
        compositeDisposable.add(
            combineNearAndSelectedCities()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(getCitiesObserver())
        )
    }

    fun getAllCities() {
        setIsLoading(true)
      //Log.d(TAG, "checkNearestCity")
        compositeDisposable.add(
            dataManager.allCities
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ cities ->
                    if (cities != null) {
                        val responseList = ArrayList<City>()
                        cities.forEach() {
                            responseList.add(it)
                        }

                        showCities.value = getViewModel(responseList.sortedBy { it.name })
                    }
                    setIsLoading(false)
                }, { throwable ->
                    setIsLoading(false)
                    throwable.printStackTrace()
                })

        )
    }

    private fun getCitiesObserver(): DisposableObserver<List<CitiesItemModel>> {
        return object : DisposableObserver<List<CitiesItemModel>>() {
            override fun onComplete() {
                setIsLoading(false)
               //Log.d(TAG, "saverObserver - onComplete")

            }

            override fun onError(e: Throwable) {
                setIsLoading(true)
            }

            override fun onNext(cities: List<CitiesItemModel>) {
               //Log.d(TAG, "saverObserver - onNext")
                showCities.value = cities

            }
        }
    }

    private fun combineNearAndSelectedCities(): Observable<List<CitiesItemModel>> {

        return Observable.zip(
            dataManager.allSelectedCities.take(1),
            dataManager.allNearestCities,
            BiFunction() { selected: List<City>, near: List<City> ->
                return@BiFunction setCitiesListsResponse(
                    selected,
                    near
                )
            })
    }

    private fun combineSearchedCities(search: String): Observable<List<CitiesItemModel>> {

        return Observable.zip(
            dataManager.getCitySelectedBySearch(search),
            dataManager.getCityNearestBySearch(search),
            BiFunction() { selected: List<City>, near: List<City> ->
                return@BiFunction setCitiesListsResponse(
                    selected,
                    near
                )
            })
    }

    private fun setCitiesListsResponse(
        selected: List<City>, near: List<City>
    ): List<CitiesItemModel> {
        val responseList = ArrayList<City>()
        selected.forEach() {
            responseList.add(it)
        }
        near.forEach() {
            responseList.add(it)
        }
        return getViewModel(responseList)
    }

    private fun getViewModel(cities: List<City>): List<CitiesItemModel> {

        val forecastItemList = ArrayList<CitiesItemModel>()

        cities.forEach {
            forecastItemList.add(CitiesItemModel(it.name, it.id.toInt()))
        }

        return forecastItemList
    }

    fun deleteCity(cityId: Int) {
        setIsLoading(true)
       //Log.d(TAG, "fetchLocalWeather")
        compositeDisposable.add(
            combineDelete(cityId)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(deleteObserver(cityId))

        )
    }

    fun addCity(cityId: Int) {
        setIsLoading(true)
       //Log.d(TAG, "addCity")
        compositeDisposable.add(
            dataManager.getCityById(cityId.toLong())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ response ->
                    if (response != null) {
                        response.setSelected()
                        setSelectedCity(response)
                    }
                    setIsLoading(false)
                }, { throwable ->
                    setIsLoading(false)
                    throwable.printStackTrace()
                })

        )
    }

    private fun setSelectedCity(city: City) {
        setIsLoading(true)
       //Log.d(TAG, "setSelectedCity: " + city.name)
        compositeDisposable.add(
            dataManager
                .updateCity(city)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ response ->
                    cityAdded.value = response
                    setIsLoading(false)
                }, { throwable ->
                    throwable.printStackTrace()
                    setIsLoading(false)
                })
        )
    }

    private fun deleteObserver(cityId: Int): DisposableObserver<Boolean> {
        return object : DisposableObserver<Boolean>() {
            override fun onComplete() {
                setIsLoading(true)
               //Log.d(TAG, "saverObserver - onComplete")
                cityDeleted.value = true

            }

            override fun onError(e: Throwable) {
                setIsLoading(true)
            }

            override fun onNext(distance: Boolean) {
               //Log.d(TAG, "saverObserver - onNext")

            }
        }
    }

    private fun combineDelete(CityId: Int): Observable<Boolean> {
        return Observable.zip(
            dataManager.deleteCity(CityId),
            dataManager.deleteForecastByCity(CityId),
            dataManager.deleteWeatherByCity(CityId),
            Function3 { forecastRes: Boolean, forecastListRes: Boolean, weatherListRes: Boolean ->
                return@Function3 forecastRes && forecastListRes && weatherListRes
            })
    }

    fun addCitiesItemsToList(showItems: List<CitiesItemModel>) {
        citiesItemViewModels.clear()
        citiesItemViewModels.addAll(showItems)
    }
}
