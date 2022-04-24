import axios from "axios"
import { useEffect, useState } from "react"
import Select from "react-dropdown-select"
import { useSelector } from "react-redux"
import { check } from "../../utils/securityUtils"
import Pagination from "../Pagination"
import MixtureCard from "./MixtureCard"
import MixtureHeader from "./MixtureHeader"

const MixtureDashboard = () => {
    const PAGE_LIMIT = 3

    const selectedLab = useSelector(state => state.selectedLab)
    const user = useSelector((state) => state.security.user)

    const [ mixtures, setMixtures ] = useState([])
    const [ projects, setProjects ] = useState([])
    
    const [ selectedProject, setSelectedProject ] = useState("")
    const [onlyAvailable, setOnlyAvailable] = useState(true)
    
    const [totalItems, setTotalItems] = useState(1)
    const [ page, setPage ] = useState(1)
    const [ size, setSize ] = useState(PAGE_LIMIT)
    
    const queryString = () => {
        let queryString = `?page=${page - 1}&size=${size}`
        queryString = `${queryString}${selectedProject ? ('&projectId=' + selectedProject.id) : ''}`
        queryString = `${queryString}${onlyAvailable ? '&available=true': ''}`
        return queryString
    }

    const isManager =  (selectedLab.key) && selectedLab.labManagers.filter(manager => manager.username === user.username).length > 0

    useEffect(() => {
        if (selectedLab) {
            axios.get(`/api/logged-in/project/${selectedLab.key}`).then(result => setProjects(result.data))
        }
        
    }, [selectedLab])

    useEffect(() => {
        check()
        if (selectedLab) {
            axios.get(`/api/mixture/${selectedLab.key}${queryString()}`)
                .then(result => {
                    setMixtures(result.data.content)
                    setTotalItems(result.data.totalItems)
                    
                })
        }
    }, [page, size, selectedProject, onlyAvailable])
   

    const deleteMixture = async mixtureId => {
        await axios.delete(`/api/mixture/${mixtureId}`)
        setMixtures(originalList => originalList.filter(mixture => mixture.id !== mixtureId))
    }

    return (
        
        <div className="container col-md-12">
            <h3 className="display-4 text-center">Mixtures</h3>
            <p className="lead text-center">List the mixtures and eluents of {selectedLab.name}</p>
            <br/>
            <div className="w-300 px-4 py-0 d-flex flex-row flex-wrap align-items-center justify-content-between">
                <div className="col-sm-4 d-flex flex-row py-2 align-items-center">
                    <Pagination 
                        totalRecords={totalItems}
                        pageLimit={PAGE_LIMIT} 
                        pageNeighbours={1}
                        onPageChanged={paginatioData => setPage(paginatioData.currentPage)}
                    />
                    <div className="pad-chckbx" >
                        <input
                            type="checkbox"
                            checked={onlyAvailable}
                            onChange={() => setOnlyAvailable(!onlyAvailable)}
                        />
                        <label className="pad-5" >Only available</label>
                        
                    </div>
                    
                
                </div>
                <div className="col-sm-8">
                    <Select
                        options={projects}
                        labelField="name"
                        valueField="name"
                        placeholder="project"
                        searchable={false}
                        clearable={false}
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={items => setSelectedProject(items[0] ? items[0] : "")}
                    />
                </div>
            </div>
            
            <MixtureHeader />
            <hr />
            {mixtures.map(mixture => 
                <MixtureCard 
                    key={mixture.id} 
                    mixture={mixture}
                    deleteMixture={deleteMixture}
                    isManager={isManager}
                />
            )}
        </div>
    )
}

export default MixtureDashboard